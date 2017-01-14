package me.jaimemartz.lobbybalancer.manager;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ConfigurationAdapter;
import net.md_5.bungee.api.config.ListenerInfo;
import net.md_5.bungee.api.config.ServerInfo;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class AdapterFix implements ConfigurationAdapter {
    private static final Map<String, ServerInfo> fakeServers = new HashMap<>();
    private static AdapterFix instance = null;
    private final ConfigurationAdapter adapter;

    public AdapterFix(ConfigurationAdapter adapter) {
        this.adapter = adapter;
    }

    public static void inject(ProxyServer server) {
        if (instance == null) {
            instance = new AdapterFix(server.getConfigurationAdapter());
        }
        server.setConfigurationAdapter(instance);
    }

    public static void addFakeServer(ServerInfo server) {
        fakeServers.put(server.getName(), server);
    }

    public static void removeFakeServer(ServerInfo server) {
        fakeServers.remove(server.getName());
    }

    public static Map<String, ServerInfo> getFakeServers() {
        return fakeServers;
    }

    @Override
    public void load() {
        adapter.load();
    }

    @Override
    public int getInt(String path, int def) {
        return adapter.getInt(path, def);
    }

    @Override
    public String getString(String path, String def) {
        return adapter.getString(path, def);
    }

    @Override
    public boolean getBoolean(String path, boolean def) {
        return adapter.getBoolean(path, def);
    }

    @Override
    public Collection<?> getList(String path, Collection<?> def) {
        return adapter.getList(path, def);
    }

    @Override
    public Map<String, ServerInfo> getServers() {
        Map<String, ServerInfo> res = adapter.getServers();
        res.putAll(fakeServers);
        return res;
    }

    @Override
    public Collection<ListenerInfo> getListeners() {
        return adapter.getListeners();
    }

    @Override
    public Collection<String> getGroups(String player) {
        return adapter.getGroups(player);
    }

    @Override
    public Collection<String> getPermissions(String group) {
        return adapter.getPermissions(group);
    }
}