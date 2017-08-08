package com.jaimemartz.playerbalancer.connection;

import com.jaimemartz.playerbalancer.manager.PlayerLocker;
import com.jaimemartz.playerbalancer.ping.ServerStatus;
import com.jaimemartz.playerbalancer.PlayerBalancer;
import com.jaimemartz.playerbalancer.configuration.ConfigEntries;
import com.jaimemartz.playerbalancer.section.ServerSection;
import com.jaimemartz.playerbalancer.utils.MessageUtils;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public abstract class ConnectionIntent {
    protected final PlayerBalancer plugin;
    protected final ProxiedPlayer player;
    protected final ServerSection section;

    public ConnectionIntent(PlayerBalancer plugin, ProxiedPlayer player, ProviderType provider, ServerSection section, List<ServerInfo> servers) {
        this.plugin = plugin;
        this.player = player;
        this.section = section;

        MessageUtils.send(player, ConfigEntries.CONNECTING_MESSAGE.get(),
                (str) -> str.replace("{section}", section.getName())
        );

        if (servers == section.getServers()) {
            throw new IllegalStateException("The servers list parameter is the same object as the section servers list, this cannot happen");
        }

        if (section.getProvider() != ProviderType.NONE) {
            ServerInfo target = this.fetchServer(plugin, player, section, provider, servers);
            if (target != null) {
                this.connect(target);
                MessageUtils.send(player, ConfigEntries.CONNECTED_MESSAGE.get(),
                        (str) -> str.replace("{target}", target.getName())
                );
            } else {
                MessageUtils.send(player, ConfigEntries.FAILURE_MESSAGE.get());
            }
        }
    }

    public ConnectionIntent(PlayerBalancer plugin, ProxiedPlayer player, ServerSection section) {
        this(plugin, player, section.getProvider(), section);
    }

    public ConnectionIntent(PlayerBalancer plugin, ProxiedPlayer player, ProviderType type, ServerSection section) {
        this(plugin, player, type, section, new ArrayList<>(section.getServers()));
    }

    public ConnectionIntent(PlayerBalancer plugin, ProxiedPlayer player, ServerSection section, List<ServerInfo> servers) {
        this(plugin, player, section.getProvider(), section, servers);
    }

    private ServerInfo fetchServer(PlayerBalancer plugin, ProxiedPlayer player, ServerSection section, ProviderType provider, List<ServerInfo> servers) {
        if (ConfigEntries.ASSIGN_TARGETS_ENABLED.get()) {
            if (ServerAssignRegistry.hasAssignedServer(player, section)) {
                ServerInfo target = ServerAssignRegistry.getAssignedServer(player, section);
                ServerStatus status = plugin.getStatusManager().getStatus(target);
                if (status.isAccessible()) {
                    return target;
                } else {
                    ServerAssignRegistry.revokeTarget(player, section);
                }
            }
        }

        int intents = ConfigEntries.SERVER_CHECK_ATTEMPTS.get();
        for (int intent = 1; intent <= intents; intent++) {
            if (servers.size() == 0) return null;
            if (servers.size() == 1) return servers.get(0);

            ServerInfo target = provider.requestTarget(plugin, section, servers, player);
            if (target == null) continue;

            ServerStatus status = plugin.getStatusManager().getStatus(target);
            if (status.isAccessible()) {
                return target;
            } else {
                servers.remove(target);
            }
        }

        return null;
    }

    public abstract void connect(ServerInfo server);

    public static void simple(PlayerBalancer plugin, ProxiedPlayer player, ServerSection section) {
        new ConnectionIntent(plugin, player, section) {
            @Override
            public void connect(ServerInfo server) {
                direct(plugin, player, server);
            }
        };
    }

    public static void direct(PlayerBalancer plugin, ProxiedPlayer player, ServerInfo server) {
        PlayerLocker.lock(player);
        player.connect(server);
        plugin.getProxy().getScheduler().schedule(plugin, () -> {
            PlayerLocker.unlock(player);
        }, 5, TimeUnit.SECONDS);
    }
}