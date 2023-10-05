package com.jaimemartz.playerbalancer.velocity.listeners;

import com.jaimemartz.playerbalancer.velocity.PlayerBalancer;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyReloadEvent;

public class ProxyReloadListener {
    private final PlayerBalancer plugin;

    public ProxyReloadListener(PlayerBalancer plugin) {
        this.plugin = plugin;
    }

    @Subscribe
    public void onReload(ProxyReloadEvent event) {
        plugin.getLogger().info("Velocity has been reloaded, reloading the plugin...");
        plugin.reloadPlugin();
    }
}