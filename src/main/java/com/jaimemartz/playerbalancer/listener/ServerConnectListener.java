package com.jaimemartz.playerbalancer.listener;

import com.jaimemartz.playerbalancer.PlayerBalancer;
import com.jaimemartz.playerbalancer.configuration.ConfigEntries;
import com.jaimemartz.playerbalancer.connection.ConnectionIntent;
import com.jaimemartz.playerbalancer.connection.ServerAssignRegistry;
import com.jaimemartz.playerbalancer.manager.PlayerLocker;
import com.jaimemartz.playerbalancer.section.ServerSection;
import com.jaimemartz.playerbalancer.utils.MessageUtils;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.util.concurrent.Callable;

public class ServerConnectListener implements Listener {
    private final PlayerBalancer plugin;

    public ServerConnectListener(PlayerBalancer plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onConnect(ServerConnectEvent event) {
        ProxiedPlayer player = event.getPlayer();
        ServerInfo target = event.getTarget();

        Callable<ServerSection> callable = () -> {
            ServerSection section = plugin.getSectionManager().getByServer(target);

            if (section != null) {
                if (PlayerLocker.isLocked(player)) {
                    return null;
                }

                //Checks only for servers (not the section server)
                if (section.getServers().contains(target)) {
                    if (section.isDummy()) {
                        return null;
                    }

                    if (player.hasPermission("playerbalancer.bypass")) {
                        MessageUtils.send(player, ConfigEntries.BYPASS_MESSAGE.get());
                        return null;
                    }

                    if (player.getServer() != null && section.getServers().contains(player.getServer().getInfo())) {
                        if (ConfigEntries.ASSIGN_TARGETS_ENABLED.get()) {
                            ServerAssignRegistry.assignTarget(player, section, target);
                        }
                        return null;
                    }
                }
            }

            return section;
        };

        try {
            ServerSection section = callable.call();
            if (section != null) {
                new ConnectionIntent(plugin, player, section) {
                    @Override
                    public void connect(ServerInfo server) {
                        if (ConfigEntries.ASSIGN_TARGETS_ENABLED.get()) {
                            ServerAssignRegistry.assignTarget(player, section, server);
                        }

                        event.setTarget(server);
                    }
                };
            }
        } catch (Exception e) {
            //Nothing to do
        }
    }
}
