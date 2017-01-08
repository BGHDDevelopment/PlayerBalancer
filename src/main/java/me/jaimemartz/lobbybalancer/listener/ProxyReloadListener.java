package me.jaimemartz.lobbybalancer.listener;

import me.jaimemartz.lobbybalancer.LobbyBalancer;
import net.md_5.bungee.api.event.ProxyReloadEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public class ProxyReloadListener implements Listener {
    private final LobbyBalancer plugin;

    public ProxyReloadListener(LobbyBalancer plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onReload(ProxyReloadEvent event) {
        plugin.getLogger().info("BungeeCord has been reloaded, reloading the plugin...");
        plugin.reloadPlugin();
    }
}