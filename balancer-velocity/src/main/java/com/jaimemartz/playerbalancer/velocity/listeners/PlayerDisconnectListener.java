package com.jaimemartz.playerbalancer.velocity.listeners;

import com.jaimemartz.playerbalancer.velocity.PlayerBalancer;
import com.jaimemartz.playerbalancer.velocity.connection.ServerAssignRegistry;
import com.jaimemartz.playerbalancer.velocity.helper.PlayerLocker;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.proxy.Player;

public class PlayerDisconnectListener {
    private final PlayerBalancer plugin;

    public PlayerDisconnectListener(PlayerBalancer plugin) {
        this.plugin = plugin;
    }

    @Subscribe
    public void onDisconnect(DisconnectEvent event) {
        Player player = event.getPlayer();
        PlayerLocker.unlock(player);

        ServerAssignRegistry.clearAsssignedServers(player);
    }
}
