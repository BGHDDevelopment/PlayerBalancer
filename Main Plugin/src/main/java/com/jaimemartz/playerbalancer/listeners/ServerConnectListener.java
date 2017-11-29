package com.jaimemartz.playerbalancer.listeners;

import com.jaimemartz.playerbalancer.PlayerBalancer;
import com.jaimemartz.playerbalancer.connection.ConnectionIntent;
import com.jaimemartz.playerbalancer.connection.ServerAssignRegistry;
import com.jaimemartz.playerbalancer.manager.PlayerLocker;
import com.jaimemartz.playerbalancer.section.ServerSection;
import com.jaimemartz.playerbalancer.utils.MessageUtils;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public class ServerConnectListener implements Listener {
    private final PlayerBalancer plugin;

    public ServerConnectListener(PlayerBalancer plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onConnect(ServerConnectEvent event) {
        ProxiedPlayer player = event.getPlayer();
        ServerInfo target = event.getTarget();

        ServerSection section = getSection(player, target);

        if (section == null)
            return;

        if (target.equals(section.getServer())) {
            event.setCancelled(true);
        }

        new ConnectionIntent(plugin, player, section) {
            @Override
            public void connect(ServerInfo server, Callback<Boolean> callback) {
                if (plugin.getSectionManager().isReiterative(section)) {
                    ServerAssignRegistry.assignTarget(player, section, server);
                }

                event.setCancelled(false);
                event.setTarget(server);
                callback.done(true, null);
            }
        };
    }

    private ServerSection getSection(ProxiedPlayer player, ServerInfo target) {
        if (player.getServer() == null && plugin.getSettings().getForcedEntrySectionProps().isEnabled()) {
            return plugin.getSectionManager().getByName(plugin.getSettings().getForcedEntrySectionProps().getSectionName());
        }

        ServerSection section = plugin.getSectionManager().getByServer(target);

        if (section != null) {
            if (PlayerLocker.isLocked(player)) {
                return null;
            }

            //Checks only for servers (not the section server)
            if (section.getServers().contains(target)) {
                if (plugin.getSectionManager().isDummy(section)) {
                    return null;
                }

                if (player.hasPermission("playerbalancer.bypass")) {
                    MessageUtils.send(player, plugin.getSettings().getMessagesProps().getBypassMessage());
                    return null;
                }

                if (player.getServer() != null && section.getServers().contains(player.getServer().getInfo())) {
                    if (plugin.getSectionManager().isReiterative(section)) {
                        ServerAssignRegistry.assignTarget(player, section, target);
                    }
                    return null;
                }
            }
        }

        return section;
    }
}
