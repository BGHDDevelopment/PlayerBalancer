package me.jaimemartz.lobbybalancer.utils;

import net.md_5.bungee.api.config.ConfigurationAdapter;
import net.md_5.bungee.api.config.ListenerInfo;
import net.md_5.bungee.api.config.ServerInfo;

import java.util.Collection;
import java.util.Map;

public class AdapterWrapper implements ConfigurationAdapter {
    private final ConfigurationAdapter wrapped;

    public AdapterWrapper(ConfigurationAdapter adapter) {
        //Prevents memory leak
        if (adapter instanceof AdapterWrapper) {
            this.wrapped = ((AdapterWrapper) adapter).wrapped;
        } else {
            this.wrapped = adapter;
        }
    }

    @Override
    public void load() {
        wrapped.load();
    }

    @Override
    public int getInt(String path, int def) {
        return wrapped.getInt(path, def);
    }

    @Override
    public String getString(String path, String def) {
        return wrapped.getString(path, def);
    }

    @Override
    public boolean getBoolean(String path, boolean def) {
        return wrapped.getBoolean(path, def);
    }

    @Override
    public Collection<?> getList(String path, Collection<?> def) {
        return wrapped.getList(path, def);
    }

    @Override
    public Map<String, ServerInfo> getServers() {
        return wrapped.getServers();
    }

    @Override
    public Collection<ListenerInfo> getListeners() {
        return wrapped.getListeners();
    }

    @Override
    public Collection<String> getGroups(String player) {
        return wrapped.getGroups(player);
    }

    @Override
    public Collection<String> getPermissions(String group) {
        return wrapped.getPermissions(group);
    }

    public ConfigurationAdapter getWrapped() {
        return wrapped;
    }
}
