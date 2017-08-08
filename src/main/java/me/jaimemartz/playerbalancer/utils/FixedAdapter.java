package me.jaimemartz.playerbalancer.utils;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ConfigurationAdapter;
import net.md_5.bungee.api.config.ServerInfo;

import java.util.HashMap;
import java.util.Map;

public class FixedAdapter extends AdapterWrapper {
    private static final Map<String, ServerInfo> fakeServers = new HashMap<>();

    static {
        ProxyServer server = ProxyServer.getInstance();
        ConfigurationAdapter adapter = server.getConfigurationAdapter();
        server.setConfigurationAdapter(new FixedAdapter(adapter));
    }

    public FixedAdapter(ConfigurationAdapter adapter) {
        super(adapter);
    }

    @Override
    public Map<String, ServerInfo> getServers() {
        Map<String, ServerInfo> res = super.getServers();
        res.putAll(fakeServers);
        return res;
    }

    public static Map<String, ServerInfo> getFakeServers() {
        return fakeServers;
    }
}