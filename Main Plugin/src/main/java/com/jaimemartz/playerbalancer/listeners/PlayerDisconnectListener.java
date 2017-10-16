package com.jaimemartz.playerbalancer.listeners;

import com.jaimemartz.playerbalancer.PlayerBalancer;
import com.jaimemartz.playerbalancer.connection.ServerAssignRegistry;
import com.jaimemartz.playerbalancer.manager.PlayerLocker;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public class PlayerDisconnectListener implements Listener {
    private final PlayerBalancer plugin;

    public PlayerDisconnectListener(PlayerBalancer plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDisconnect(PlayerDisconnectEvent event) {
        ProxiedPlayer player = event.getPlayer();
        PlayerLocker.unlock(player);

        ServerAssignRegistry.clearAsssignedServers(player);
    }
}
