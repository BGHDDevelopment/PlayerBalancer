package com.jaimemartz.playerbalancer.section;

import com.jaimemartz.playerbalancer.PlayerBalancer;
import com.jaimemartz.playerbalancer.settings.props.features.BalancerProps;
import com.jaimemartz.playerbalancer.settings.props.shared.SectionProps;
import com.jaimemartz.playerbalancer.utils.FixedAdapter;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class SectionManager {
    private final PlayerBalancer plugin;
    private final BalancerProps props;
    private ServerSection principal;

    private final Map<String, ServerSection> sections = Collections.synchronizedMap(new HashMap<>());
    private final Map<ServerInfo, ServerSection> servers = Collections.synchronizedMap(new HashMap<>());

    public SectionManager(PlayerBalancer plugin) {
        this.props = plugin.getSettings().getBalancerProps();
        this.plugin = plugin;
    }

    public void load() throws RuntimeException {
        plugin.getLogger().info("Executing section initialization stages, this may take a while...");
        long starting = System.currentTimeMillis();

        for (Stage stage : stages) {
            plugin.getLogger().info("Executing stage \"" + stage.title + "\"");
            stage.execute();
        }

        long ending = System.currentTimeMillis() - starting;
        plugin.getLogger().info(String.format("A total of %s section(s) have been loaded in %sms", sections.size(), ending));
    }

    public void flush() {
        plugin.getLogger().info("Flushing section storage because of plugin shutdown");
        sections.forEach((key, value) -> {
            value.setValid(false);

            if (value.getCommand() != null) {
                SectionCommand command = value.getCommand();
                plugin.getProxy().getPluginManager().unregisterCommand(command);
            }

            if (value.getServer() != null) {
                ServerInfo server = value.getServer();
                plugin.getProxy().getServers().remove(server.getName());
            }
        });

        principal = null;
        sections.clear();
        servers.clear();
    }

    public void register(ServerInfo server, ServerSection section) {
        if (servers.containsKey(server)) {
            if (isDummy(section)) {
                return;
            }

            ServerSection other = servers.get(server);
            throw new IllegalArgumentException(String.format(
                    "The server \"%s\" is already in the section \"%s\"",
                    server.getName(),
                    other.getName()
            ));
        }

        plugin.getLogger().info(String.format("Registering server \"%s\" to section \"%s\"",
                server.getName(),
                section.getName()
        ));

        servers.put(server, section);

    }

    public ServerSection getByName(String name) {
        if (name == null) {
            return null;
        }

        return sections.get(name);
    }

    public ServerSection getByServer(ServerInfo server) {
        if (server == null) {
            return null;
        }

        return servers.get(server);
    }

    public ServerSection getByPlayer(ProxiedPlayer player) {
        if (player == null) {
            return null;
        }

        Server server = player.getServer();

        if (server == null) {
            return null;
        }

        return getByServer(server.getInfo());
    }

    private final Stage[] stages = {
            new SectionStage("Constructing sections") {
                @Override
                public void execute(String sectionName, SectionProps sectionProps, ServerSection section) throws RuntimeException {
                    ServerSection object = new ServerSection(sectionName, sectionProps);
                    sections.put(sectionName, object);
                }
            },
            new Stage("Processing principal section") {
                @Override
                public void execute() {
                    principal = sections.get(props.getPrincipalSectionName());
                    if (principal == null) {
                        throw new IllegalArgumentException(String.format(
                                "Could not set principal section, there is no section called \"%s\"",
                                props.getPrincipalSectionName()
                        ));
                    }
                }
            },
            new SectionStage("Processing parents") {
                @Override
                public void execute(String sectionName, SectionProps sectionProps, ServerSection section) throws RuntimeException {
                    if (sectionProps.getParentName() != null) {
                        ServerSection parent = getByName(sectionProps.getParentName());
                        if (principal.equals(section) && parent == null) {
                            throw new IllegalArgumentException(String.format(
                                    "The section \"%s\" has an invalid parent set",
                                    section.getName()
                            ));
                        } else {
                            section.setParent(parent);
                        }
                    }
                }
            },
            new SectionStage("Validating parents") {
                @Override
                public void execute(String sectionName, SectionProps sectionProps, ServerSection section) throws RuntimeException {
                    ServerSection parent = section.getParent();
                    if (parent != null && parent.getParent() == section) {
                        throw new IllegalStateException(String.format(
                                "The sections \"%s\" and \"%s\" are parents of each other",
                                section.getName(),
                                parent.getName()
                        ));
                    }
                }
            },
            new SectionStage("Validating providers") {
                @Override
                public void execute(String sectionName, SectionProps sectionProps, ServerSection section) throws RuntimeException {
                    if (sectionProps.getProvider() == null) {
                        ServerSection current = section.getParent();
                        if (current != null) {
                            while (current.getExplicitProvider() == null) {
                                current = current.getParent();
                            }

                            plugin.getLogger().info(String.format(
                                    "The section \"%s\" inherits the provider from the section \"%s\"",
                                    section.getName(),
                                    current.getName()
                            ));

                            section.setInherited(true);
                        }
                    } else {
                        section.setInherited(false);
                    }
                }
            },
            new SectionStage("Calculating positions") {
                @Override
                public void execute(String sectionName, SectionProps sectionProps, ServerSection section) throws RuntimeException {
                    section.setPosition(calculatePosition(section));
                }
            },
            new SectionStage("Resolving servers") {
                @Override
                public void execute(String sectionName, SectionProps sectionProps, ServerSection section) throws RuntimeException {
                    section.getServers().addAll(calculateServers(section));
                }
            },
            new SectionStage("Section server processing") {
                @Override
                public void execute(String sectionName, SectionProps sectionProps, ServerSection section) throws RuntimeException {
                    if (sectionProps.getServerName() != null) {
                        SectionServer server = new SectionServer(props, section);
                        section.setServer(server);
                        plugin.getSectionManager().register(server, section);
                        FixedAdapter.getFakeServers().put(server.getName(), server);
                        plugin.getProxy().getServers().put(server.getName(), server);
                    }
                }
            },
            new SectionStage("Section command processing") {
                @Override
                public void execute(String sectionName, SectionProps sectionProps, ServerSection section) throws RuntimeException {
                    if (sectionProps.getCommandProps() != null) {
                        SectionCommand command = new SectionCommand(plugin, section);
                        section.setCommand(command);
                        plugin.getProxy().getPluginManager().registerCommand(plugin, command);
                    }
                }
            },
            new SectionStage("Finishing loading sections") {
                @Override
                public void execute(String sectionName, SectionProps sectionProps, ServerSection section) throws RuntimeException {
                    section.setValid(true);
                }
            },
    };

    public Set<ServerInfo> calculateServers(ServerSection section) {
        Set<ServerInfo> results = new HashSet<>();

        section.getProps().getServerEntries().forEach(entry -> {
            Pattern pattern = Pattern.compile(entry);
            AtomicBoolean matches = new AtomicBoolean(false);
            plugin.getProxy().getServers().forEach((name, server) -> {
                Matcher matcher = pattern.matcher(name);
                if (matcher.matches()) {
                    plugin.getLogger().info(String.format("Found a match with \"%s\" for entry \"%s\"", name, entry));
                    results.add(server);
                    register(server, section);
                    matches.set(true);
                }
            });

            if (!matches.get()) {
                plugin.getLogger().warning(String.format("Could not match any servers with the entry \"%s\"", entry));
            }
        });

        plugin.getLogger().info(String.format("Recognized %s server(s) out of %s in the section \"%s\"",
                results.size(),
                section.getProps().getServerEntries(),
                section.getName()
        ));

        return results;
    }

    public int calculatePosition(ServerSection section) {
        ServerSection current = section;

        //Calculate above principal
        int iterations = 0;
        while (current != null) {
            if (current == principal) {
                return iterations;
            }

            current = current.getParent();
            iterations++;
        }

        //Calculate below principal
        if (principal != null) {
            iterations = 0;
            current = principal;
            while (current != null) {
                if (current.equals(section)) {
                    return iterations;
                }

                current = current.getParent();
                iterations--;
            }
        }

        return iterations;
    }

    public ServerSection getPrincipal() {
        return principal;
    }

    public boolean isPrincipal(ServerSection section) {
        return principal.equals(section);
    }

    public boolean isDummy(ServerSection section) {
        List<String> dummySections = props.getDummySectionNames();
        return dummySections.contains(section.getName());
    }

    public boolean isReiterative(ServerSection section) {
        List<String> reiterativeSections = props.getReiterativeSectionNames();
        return reiterativeSections.contains(section.getName());
    }

    public Optional<ServerSection> getBind(Map<String, String> rules, ServerSection section) {
        String bind = rules.get(section.getName());
        ServerSection res = this.getByName(bind);
        return Optional.ofNullable(res);
    }

    public Map<String, ServerSection> getSections() {
        return sections;
    }

    private abstract class Stage {
        private final String title;

        private Stage(String title) {
            this.title = title;
        }

        public abstract void execute() throws RuntimeException;
    }

    private abstract class SectionStage extends Stage {
        private SectionStage(String title) {
            super(title);
        }

        @Override
        public void execute() throws RuntimeException {
            props.getSectionProps().forEach((name, props) -> {
                execute(name, props, sections.get(name));
            });
        }

        public abstract void execute(
                String sectionName,
                SectionProps sectionProps,
                ServerSection section
        ) throws RuntimeException;
    }
}
