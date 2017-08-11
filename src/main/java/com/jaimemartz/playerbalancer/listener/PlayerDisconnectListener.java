package com.jaimemartz.playerbalancer.listener;

import com.jaimemartz.playerbalancer.connection.ServerAssignRegistry;
import com.jaimemartz.playerbalancer.manager.PlayerLocker;
import com.jaimemartz.playerbalancer.settings.Settings;
import com.jaimemartz.playerbalancer.settings.types.GeneralProperties;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import javax.inject.Inject;

public class PlayerDisconnectListener implements Listener {
    @Inject
    private Settings settings;

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDisconnect(PlayerDisconnectEvent event) {
        ProxiedPlayer player = event.getPlayer();
        PlayerLocker.unlock(player);

        //Delete this if we want to keep assigned groups even when leaving
        if (settings.getProperty(GeneralProperties.ASSIGN_TARGETS)) {
            ServerAssignRegistry.clearAsssignedServers(player);
        }
    }
}
