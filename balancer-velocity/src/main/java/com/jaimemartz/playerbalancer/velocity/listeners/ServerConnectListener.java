package com.jaimemartz.playerbalancer.velocity.listeners;

import com.jaimemartz.playerbalancer.velocity.PlayerBalancer;
import com.jaimemartz.playerbalancer.velocity.connection.ConnectionIntent;
import com.jaimemartz.playerbalancer.velocity.connection.ServerAssignRegistry;
import com.jaimemartz.playerbalancer.velocity.helper.PlayerLocker;
import com.jaimemartz.playerbalancer.velocity.section.ServerSection;
import com.jaimemartz.playerbalancer.velocity.utils.MessageUtils;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;

import java.util.function.Consumer;

public class ServerConnectListener {
    private final PlayerBalancer plugin;

    public ServerConnectListener(PlayerBalancer plugin) {
        this.plugin = plugin;
    }

    @Subscribe
    public void onConnect(ServerPreConnectEvent event) {
        Player player = event.getPlayer();
        RegisteredServer target = event.getOriginalServer();

        if (PlayerLocker.isLocked(player))
            return;

        ServerSection section = getSection(player, target);

        if (section == null)
            return;

        new ConnectionIntent(plugin, player, section) {
            @Override
            public void connect(ServerInfo server, Consumer<Boolean> callback) {
                if (plugin.getSectionManager().isReiterative(section)) {
                    ServerAssignRegistry.assignTarget(player, section, server);
                }

                plugin.getProxyServer().getServer(server.getName()).ifPresent(registeredServer -> {
                    event.setResult(ServerPreConnectEvent.ServerResult.allowed(registeredServer));
                    callback.accept(true);
                });
            }
        }.execute();
    }

    private ServerSection getSection(Player player, RegisteredServer target) {
        ServerSection section = plugin.getSectionManager().getByServer(target);

        if (section != null) {
            // Checks only for servers (not the section server)
            if (!target.equals(section.getServer())) {
                if (plugin.getSectionManager().isDummy(section)) {
                    return null;
                }

                if (player.hasPermission("playerbalancer.bypass")) {
                    MessageUtils.send(player, plugin.getSettings().getMessagesProps().getBypassMessage());
                    return null;
                }

                ServerConnection serverConnection = player.getCurrentServer().orElse(null);
                if (serverConnection != null && section.getServers().contains(serverConnection.getServer())) {
                    if (plugin.getSectionManager().isReiterative(section)) {
                        ServerAssignRegistry.assignTarget(player, section, target.getServerInfo());
                    }
                    return null;
                }
            }
        }

        return section;
    }
}
