package com.jaimemartz.playerbalancer.section;

import com.jaimemartz.playerbalancer.PlayerBalancer;
import com.jaimemartz.playerbalancer.settings.props.features.BalancerProps;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.scheduler.ScheduledTask;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SectionManager {
    private final PlayerBalancer plugin;
    private ScheduledTask updateTask;
    private ServerSection principal;

    private final Map<String, ServerSection> sections = new HashMap<>();
    private final Map<ServerInfo, ServerSection> servers = new HashMap<>();

    public SectionManager(PlayerBalancer plugin) {
        this.plugin = plugin;
    }

    public void load() throws RuntimeException {
        plugin.getLogger().info("Loading sections from the config, this may take a while...");
        long starting = System.currentTimeMillis();

        plugin.getSettings().getBalancerProps().getSectionProps().forEach((name, prop) -> {
            plugin.getLogger().info(String.format("Construction of section with name \"%s\"", name));
            ServerSection object = new ServerSection(name, prop);
            sections.put(name, object);
        });

        //todo validate principal section
        //todo validate dummy sections

        sections.forEach((name, section) -> {
            //load section
        });

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

    /**
     * Calculates the position of a section in relation to other sections
     * This is supposed to be called on section construction
     * @param section the section we want to get the position of
     * @return the position of {@param section}
     */
    private int calculatePosition(ServerSection section) {
        //Calculate above principal
        int iterations = 0;
        ServerSection current = section;
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
        return section.equals(principal);
    }

    public boolean isDummy(ServerSection section) {
        BalancerProps props = plugin.getSettings().getBalancerProps();
        return props.getDummySectionNames().contains(section.getName());
    }

    public boolean isReiterative(ServerSection section) {
        BalancerProps props = plugin.getSettings().getBalancerProps();
        return props.getReiterativeSectionNames().contains(section.getName());
    }

    public Map<String, ServerSection> getSections() {
        return sections;
    }

    public Optional<ServerSection> getBind(Map<String, String> rules, ServerSection section) {
        return Optional.ofNullable(getByName(rules.get(section.getName())));
    }
}
