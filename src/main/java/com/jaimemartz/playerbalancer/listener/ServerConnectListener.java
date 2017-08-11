package com.jaimemartz.playerbalancer.listener;

import com.jaimemartz.playerbalancer.PlayerBalancer;
import com.jaimemartz.playerbalancer.settings.Settings;
import com.jaimemartz.playerbalancer.settings.types.SectionsHolder;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import javax.inject.Inject;

public class ServerConnectListener implements Listener {
    @Inject
    private Settings settings;

    @Inject
    private SectionsHolder holder;

    private final PlayerBalancer plugin;

    public ServerConnectListener(PlayerBalancer plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onConnect(ServerConnectEvent event) {
        /*
        ProxiedPlayer player = event.getPlayer();
        ServerInfo target = event.getTarget();

        Callable<ServerSection> callable = () -> {
            ServerSection section = holder.getByServer(target);

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
                        MessageUtils.send(player, settings.getProperty(MessageProperties.PLAYER_BYPASS));
                        return null;
                    }

                    if (player.getServer() != null && section.getServers().contains(player.getServer().getInfo())) {
                        if (settings.getProperty(GeneralProperties.ASSIGN_TARGETS)) {
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
                    public void connect(ServerInfo server, Callback<Boolean> callback) {
                        if (settings.getProperty(GeneralProperties.ASSIGN_TARGETS)) {
                            ServerAssignRegistry.assignTarget(player, section, server);
                        }

                        event.setTarget(server);
                        callback.done(true, null);
                    }
                };
            }
        } catch (Exception e) {
            //Nothing to do
        }
        */
    }
}
