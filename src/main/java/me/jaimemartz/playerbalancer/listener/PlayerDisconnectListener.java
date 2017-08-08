package me.jaimemartz.playerbalancer.listener;

import me.jaimemartz.playerbalancer.PlayerBalancer;
import me.jaimemartz.playerbalancer.configuration.ConfigEntries;
import me.jaimemartz.playerbalancer.connection.ServerAssignRegistry;
import me.jaimemartz.playerbalancer.manager.PlayerLocker;
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

        //Delete this if we want to keep assigned groups even when leaving
        if (ConfigEntries.ASSIGN_TARGETS_ENABLED.get()) {
            ServerAssignRegistry.clearAsssignedServers(player);
        }
    }
}
