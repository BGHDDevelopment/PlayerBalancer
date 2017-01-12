package me.jaimemartz.lobbybalancer.listener;

import me.jaimemartz.lobbybalancer.LobbyBalancer;
import me.jaimemartz.lobbybalancer.configuration.ConfigEntries;
import me.jaimemartz.lobbybalancer.connection.ServerAssignRegistry;
import me.jaimemartz.lobbybalancer.manager.PlayerLocker;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public class PlayerDisconnectListener implements Listener {
    private final LobbyBalancer plugin;

    public PlayerDisconnectListener(LobbyBalancer plugin) {
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
