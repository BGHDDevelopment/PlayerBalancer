package me.jaimemartz.lobbybalancer.section;

import me.jaimemartz.lobbybalancer.LobbyBalancer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.config.Configuration;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SectionManager {
    private ServerSection principal;
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
            ServerSection object = new ServerSection(name, section, this);
            sectionStorage.put(name, object);
        });

        sectionStorage.forEach((name, section) -> {
            plugin.getLogger().info(String.format("Pre-Initialization of section with name \"%s\"", name));
            section.preInit(plugin);
        });

        sectionStorage.forEach((name, section) -> {
            plugin.getLogger().info(String.format("Initialization of section with name \"%s\"", name));
            section.load(plugin);
        });

        sectionStorage.forEach((name, section) -> {
            plugin.getLogger().info(String.format("Post-Initialization of section with name \"%s\"", name));
            section.postInit(plugin);
        });

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
        sectionStorage.clear();
        sectionServers.clear();
    }

    void register(ServerInfo server, ServerSection section) {
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
        return Collections.unmodifiableMap(sectionStorage);
    }

    public ServerSection getPrincipal() {
        return principal;
    }

    protected void setPrincipal(ServerSection principal) {
        this.principal = principal;
    }
}
