package me.jaimemartz.lobbybalancer.section;

import me.jaimemartz.lobbybalancer.LobbyBalancer;
import me.jaimemartz.lobbybalancer.configuration.ConfigEntries;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.md_5.bungee.config.Configuration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SectionManager {
    private ServerSection principal;
    private ScheduledTask updateTask;
    private final LobbyBalancer plugin;
    private final Map<String, ServerSection> sectionStorage = new ConcurrentHashMap<>();
    private final Map<ServerInfo, ServerSection> sectionServers = new ConcurrentHashMap<>();

    public SectionManager(LobbyBalancer plugin) {
        this.plugin = plugin;
    }

    public void load() throws RuntimeException {
        plugin.getLogger().info("Loading sections from the config, this may take a while...");
        long starting = System.currentTimeMillis();

        Configuration sections = plugin.getConfig().getSection("sections");
        sections.getKeys().forEach(name -> {
            plugin.getLogger().info(String.format("Construction of section with name \"%s\"", name));
            Configuration section = sections.getSection(name);
            ServerSection object = new ServerSection(plugin, name, section);
            sectionStorage.put(name, object);
        });

        sectionStorage.forEach((name, section) -> {
            plugin.getLogger().info(String.format("Pre-Initialization of section with name \"%s\"", name));
            section.preInit();
        });

        sectionStorage.forEach((name, section) -> {
            plugin.getLogger().info(String.format("Initialization of section with name \"%s\"", name));
            section.load();
        });

        sectionStorage.forEach((name, section) -> {
            plugin.getLogger().info(String.format("Post-Initialization of section with name \"%s\"", name));
            section.postInit();
        });

        if (ConfigEntries.SERVERS_UPDATE.get()) {
            updateTask = plugin.getProxy().getScheduler().schedule(plugin, () -> {
                sectionStorage.forEach((name, section) -> {
                    section.getConfiguration().getStringList("servers").forEach(entry -> {
                        Pattern pattern = Pattern.compile(entry);
                        plugin.getProxy().getServers().forEach((key, value) -> {
                            Matcher matcher = pattern.matcher(key);
                            if (matcher.matches()) {
                                if (!section.getServers().contains(value)) {
                                    plugin.getLogger().info(String.format("Found a new match with \"%s\" for entry \"%s\"", key, entry));
                                    this.register(value, section);
                                    section.getServers().add(value);
                                }
                            }
                        });
                    });
                });
            }, 1, 1, TimeUnit.MINUTES);
        }

        long ending = System.currentTimeMillis() - starting;
        plugin.getLogger().info(String.format("A total of %s section(s) have been loaded in %sms", sectionStorage.size(), ending));
    }

    public void flush() {
        plugin.getLogger().info("Flushing section storage because of plugin shutdown");
        sectionStorage.forEach((key, value) -> {
            value.setValid(false);

            if (value.hasCommand()) {
                SectionCommand command = value.getCommand();
                plugin.getProxy().getPluginManager().unregisterCommand(command);
            }

            if (value.hasServer()) {
                ServerInfo server = value.getServer();
                plugin.getProxy().getServers().remove(server.getName());
            }
        });

        principal = null;
        if (updateTask != null) {
            updateTask.cancel();
            updateTask = null;
        }
        sectionStorage.clear();
        sectionServers.clear();
    }

    public void register(ServerInfo server, ServerSection section) {
        if (sectionServers.containsKey(server)) {
            ServerSection other = sectionServers.get(server);
            throw new IllegalArgumentException(String.format("The server \"%s\" is already in the section \"%s\"", server.getName(), other.getName()));
        }

        plugin.getLogger().info(String.format("Registering server \"%s\" to section \"%s\"", server.getName(), section.getName()));
        sectionServers.put(server, section);

    }

    public ServerSection getByName(String name) {
        if (name == null) return null;
        return sectionStorage.get(name);
    }

    public ServerSection getByServer(ServerInfo server) {
        if (server == null) return null;
        return sectionServers.get(server);
    }

    public Map<String, ServerSection> getSections() {
        return sectionStorage;
    }

    public ServerSection getPrincipal() {
        return principal;
    }

    public void setPrincipal(ServerSection principal) {
        this.principal = principal;
    }

    public boolean hasPrincipal() {
        return principal != null;
    }
}
