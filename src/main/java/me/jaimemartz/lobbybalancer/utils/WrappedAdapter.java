package me.jaimemartz.lobbybalancer.utils;

import net.md_5.bungee.api.config.ConfigurationAdapter;
import net.md_5.bungee.api.config.ListenerInfo;
import net.md_5.bungee.api.config.ServerInfo;

import java.util.Collection;
import java.util.Map;

public class WrappedAdapter implements ConfigurationAdapter {
    private final ConfigurationAdapter adapter;

    public WrappedAdapter(ConfigurationAdapter adapter) {
        this.adapter = adapter;
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
        return adapter.getServers();
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
