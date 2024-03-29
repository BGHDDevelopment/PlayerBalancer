package com.jaimemartz.playerbalancer.velocity.section;

import com.jaimemartz.playerbalancer.velocity.PlayerBalancer;
import com.jaimemartz.playerbalancer.velocity.settings.props.features.BalancerProps;
import com.jaimemartz.playerbalancer.velocity.settings.props.shared.SectionProps;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
import com.velocitypowered.api.scheduler.ScheduledTask;
import lombok.Getter;

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SectionManager {
    private final PlayerBalancer plugin;
    private final BalancerProps props;
    @Getter
    private ServerSection principal;
    private ScheduledTask refreshTask;

    @Getter
    private final Map<String, ServerSection> sections = Collections.synchronizedMap(new HashMap<>());
    @Getter
    private final Map<RegisteredServer, ServerSection> servers = Collections.synchronizedMap(new HashMap<>());

    private static final Map<String, Stage> stages = Collections.synchronizedMap(new LinkedHashMap<>());

    public SectionManager(PlayerBalancer plugin) {
        this.props = plugin.getSettings().getFeaturesProps().getBalancerProps();
        this.plugin = plugin;
    }

    public void load() throws RuntimeException {
        plugin.getLogger().info("Executing section initialization stages, this may take a while...");
        long starting = System.currentTimeMillis();

        stages.put("constructing-sections", new SectionStage("Constructing sections") {
            @Override
            public void execute(String sectionName, SectionProps sectionProps, ServerSection section) throws RuntimeException {
                ServerSection object = new ServerSection(sectionName, sectionProps);
                sections.put(sectionName, object);
            }
        });

        stages.put("processing-principal-section", new Stage("Processing principal section") {
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
        });

        stages.put("processing-parents", new SectionStage("Processing parents") {
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
        });

        stages.put("validating-parents", new SectionStage("Validating parents") {
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
        });

        stages.put("validating-providers", new SectionStage("Validating providers") {
            @Override
            public void execute(String sectionName, SectionProps sectionProps, ServerSection section) throws RuntimeException {
                if (sectionProps.getProvider() == null) {
                    ServerSection current = section.getParent();
                    if (current != null) {
                        while (current.getProps().getProvider() == null) {
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
        });

        stages.put("calculating-positions", new SectionStage("Calculating positions") {
            @Override
            public void execute(String sectionName, SectionProps sectionProps, ServerSection section) throws RuntimeException {
                section.setPosition(calculatePosition(section));
            }
        });

        stages.put("resolving-servers", new SectionStage("Resolving servers") {
            @Override
            public void execute(String sectionName, SectionProps sectionProps, ServerSection section) throws RuntimeException {
                calculateServers(section);
            }
        });

        stages.put("section-server-processing", new SectionStage("Section server processing") {
            @Override
            public void execute(String sectionName, SectionProps sectionProps, ServerSection section) throws RuntimeException {
                if (sectionProps.getServerName() != null) {
                    ServerInfo serverInfo = new ServerInfo("@" + sectionProps.getServerName(), new InetSocketAddress("0.0.0.0", (int) Math.floor(Math.random() * (0xFFFF + 1))));
                    RegisteredServer sectionServer = plugin.getProxyServer().registerServer(serverInfo);
                    plugin.getSectionManager().registerServer(sectionServer, section);
                    section.setServer(sectionServer);
                }
            }
        });

        stages.put("section-command-processing", new SectionStage("Section command processing") {
            @Override
            public void execute(String sectionName, SectionProps sectionProps, ServerSection section) throws RuntimeException {
                if (sectionProps.getCommandProps() != null) {
                    SectionCommand command = new SectionCommand(plugin, section);
                    section.setCommand(command);
                    CommandMeta sectionCommandMeta = plugin.getProxyServer().getCommandManager()
                            .metaBuilder(command.getCommandProps().getName())
                            .aliases(command.getCommandProps().getAliasesArray())
                            .build();
                    plugin.getProxyServer().getCommandManager().register(sectionCommandMeta, command);
                }
            }
        });

        stages.put("finishing-loading", new SectionStage("Finishing loading sections") {
            @Override
            public void execute(String sectionName, SectionProps sectionProps, ServerSection section) throws RuntimeException {
                section.setValid(true);
            }
        });

        stages.forEach((name, stage) -> {
            plugin.getLogger().info("Executing stage \"" + stage.title + "\"");
            stage.execute();
        });

        if (plugin.getSettings().getFeaturesProps().getServerRefreshProps().isEnabled()) {
            plugin.getLogger().info("Starting automatic server refresh task");
            refreshTask = plugin.getProxyServer().getScheduler().buildTask(plugin, () -> {
                props.getSectionProps().forEach((name, props) -> {
                    ServerSection section = sections.get(name);
                    calculateServers(section);
                });
            }).delay(plugin.getSettings().getFeaturesProps().getServerRefreshProps().getDelay(), TimeUnit.MILLISECONDS)
                    .repeat(plugin.getSettings().getFeaturesProps().getServerRefreshProps().getInterval(), TimeUnit.MILLISECONDS)
                    .schedule();
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
                plugin.getProxyServer().getCommandManager().unregister(command.getCommandProps().getName());
            }

            if (value.getServer() != null) {
                ServerInfo server = value.getServer().getServerInfo();

                plugin.getProxyServer().unregisterServer(server);
            }
        });

        if (refreshTask != null) {
            refreshTask.cancel();
            refreshTask = null;
        }

        principal = null;
        sections.clear();
        servers.clear();
    }

    public ServerSection getByName(String name) {
        if (name == null) {
            return null;
        }

        return sections.get(name);
    }

    public ServerSection getByServer(RegisteredServer server) {
        if (server == null) {
            return null;
        }

        return servers.get(server);
    }

    public ServerSection getByPlayer(Player player) {
        if (player == null) {
            return null;
        }

        Optional<ServerConnection> server = player.getCurrentServer();

        return server.map(serverConnection -> getByServer(serverConnection.getServer())).orElse(null);

    }

    public void registerServer(RegisteredServer server, ServerSection section) {
        if (!isDummy(section)) {
            //  Checking for already we already added this server to other section
            //  This can only happen if another non dummy section registers this server
            if (servers.containsKey(server)) {
                ServerSection other = servers.get(server);
                throw new IllegalArgumentException(String.format(
                        "The server \"%s\" is already in the section \"%s\"",
                        server.getServerInfo().getName(),
                        other.getName()
                ));
            }

            plugin.getLogger().info(String.format("Registering server \"%s\" to section \"%s\"",
                    server.getServerInfo().getName(),
                    section.getName()
            ));

            servers.put(server, section);
        }
    }

    public void calculateServers(ServerSection section) {
        Set<RegisteredServer> results = new HashSet<>();

        // Searches for matches
        section.getProps().getServerEntries().forEach(entry -> {
            Pattern pattern = Pattern.compile(entry);
            plugin.getProxyServer().getAllServers().forEach((server) -> {
                Matcher matcher = pattern.matcher(server.getServerInfo().getName());
                if (matcher.matches()) {
                    results.add(server);
                }
            });
        });

        // Checks if there are servers previously matched that are no longer valid
        section.getServers().forEach(server -> {
            ServerInfo info = server.getServerInfo();
            if (!results.contains(server)) {
                servers.remove(info);
                section.getServers().remove(server);
                plugin.getLogger().info(String.format("Removed the server %s from %s as it does no longer exist",
                        info.getName(), section.getName()
                ));
            }
        });

        // Add matched servers to the section
        int addedServers = 0;
        for (RegisteredServer server : results) {
            if (!section.getServers().contains(server)) {
                section.getServers().add(server);
                registerServer(server, section);
                addedServers++;
                plugin.getLogger().info(String.format("Added the server %s to %s",
                        server.getServerInfo().getName(), section.getName()
                ));
            }
        }

        if (addedServers > 0) {
            plugin.getLogger().info(String.format("Recognized %s server%s in the section \"%s\"",
                    addedServers,
                    addedServers != 1 ? "s" : "",
                    section.getName()
            ));
        }
    }

    public int calculatePosition(ServerSection section) {
        ServerSection current = section;

        // Calculate above principal
        int iterations = 0;
        while (current != null) {
            if (current == principal) {
                return iterations;
            }

            current = current.getParent();
            iterations++;
        }

        // Calculate below principal
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

    public Stage getStage(String name) {
        return stages.get(name);
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
