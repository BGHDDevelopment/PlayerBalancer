package com.jaimemartz.playerbalancer.section;

import com.google.common.base.Preconditions;
import com.jaimemartz.playerbalancer.PlayerBalancer;
import com.jaimemartz.playerbalancer.settings.props.features.BalancerProps;
import com.jaimemartz.playerbalancer.utils.FixedAdapter;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.scheduler.ScheduledTask;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SectionManager {
    private final PlayerBalancer plugin;
    private final BalancerProps props;
    private ScheduledTask updateTask;

    private final Map<String, ServerSection> sections = new HashMap<>();
    private final Map<ServerInfo, ServerSection> servers = new HashMap<>();

    public SectionManager(PlayerBalancer plugin) {
        this.props = plugin.getSettings().getBalancerProps();
        this.plugin = plugin;
    }

    public void load() throws RuntimeException {
        plugin.getLogger().info("Loading sections from the config, this may take a while...");
        long starting = System.currentTimeMillis();

        props.getSectionProps().forEach((name, prop) -> {
            plugin.getLogger().info(String.format("Construction of section with name \"%s\"", name));
            ServerSection object = new ServerSection(name, prop);
            sections.put(name, object);
        });

        Preconditions.checkNotNull(this.getPrincipal(),
                "Could not set principal section, there is no section named \"%s\"",
                props.getPrincipalSectionName()
        );

        sections.forEach(this::processSection);

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

        if (updateTask != null) {
            updateTask.cancel();
            updateTask = null;
        }

        sections.clear();
        servers.clear();
    }

    public void register(ServerInfo server, ServerSection section) {
        if (servers.containsKey(server)) {
            if (isDummy(section)) {
                return;
            }

            ServerSection other = servers.get(server);
            throw new IllegalArgumentException(String.format("The server \"%s\" is already in the section \"%s\"", server.getName(), other.getName()));
        }

        plugin.getLogger().info(String.format("Registering server \"%s\" to section \"%s\"", server.getName(), section.getName()));
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

    public void processSection(String sectionName, ServerSection section) throws RuntimeException {
        plugin.getLogger().info(String.format("Loading section with name \"%s\"", sectionName));

        Optional.ofNullable(section.getProps().getParentName()).ifPresent(parentName -> {
            ServerSection parent = getByName(parentName);
            if (parent == null) {
                throw new IllegalArgumentException(String.format("The section \"%s\" has an invalid parent set", sectionName));
            } else {
                section.setParent(parent);
            }
        });

        Optional.ofNullable(section.getParent()).ifPresent(parent -> {
            if (parent.getProps().getParentName().equals(sectionName)) {
                throw new IllegalStateException(String.format("The sections \"%s\" and \"%s\" are parents of each other",
                        sectionName,
                        section.getParent().getName()
                ));
            }
        });

        Set<ServerInfo> servers = calculateServers(section);
        section.getServers().addAll(servers);

        //TODO move this to other stage
        if (section.getProps().getProvider() != null) {
            section.setInherited(false);
        } else {
            section.setInherited(true);

            if (section.getImplicitProvider() != null) {

            } else {
                throw new IllegalStateException(String.format("The section \"%s\" does not have a provider", sectionName));
            }
        }

        section.setPosition(calculatePosition(section));

        Optional.ofNullable(section.getProps().getServerName()).ifPresent(serverName -> {
            int port = (int) Math.floor(Math.random() * (0xFFFF + 1)); //Get a random valid port for our fake server
            ServerInfo server = plugin.getProxy().constructServerInfo(
                    "@" + serverName,
                    new InetSocketAddress("0.0.0.0", port),
                    String.format("Server of Section %s", sectionName),
                    false);
            section.setServer(server);
            plugin.getSectionManager().register(server, section);
            FixedAdapter.getFakeServers().put(server.getName(), server);
            plugin.getProxy().getServers().put(server.getName(), server);
        });

        Optional.ofNullable(section.getProps().getCommand()).ifPresent(props -> {
            SectionCommand command = new SectionCommand(plugin, props, section);
            section.setCommand(command);
            plugin.getProxy().getPluginManager().registerCommand(plugin, command);
        });

        section.setValid(true);
    }

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

        plugin.getLogger().info(String.format("Recognized %s server(s) out of %s entries on the section \"%s\"",
                servers.size(),
                section.getProps().getServerEntries(),
                section.getName()
        ));

        return results;
    }

    public int calculatePosition(ServerSection section) {
        ServerSection principal = this.getPrincipal();
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

    //maybe store this as a variable?
    public ServerSection getPrincipal() {
        return getByName(props.getPrincipalSectionName());
    }

    public boolean isPrincipal(ServerSection section) {
        return getPrincipal().equals(section);
    }

    public Map<String, ServerSection> getSections() {
        return sections;
    }
}
