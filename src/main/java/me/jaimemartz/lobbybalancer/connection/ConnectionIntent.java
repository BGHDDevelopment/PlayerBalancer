package me.jaimemartz.lobbybalancer.connection;

import me.jaimemartz.faucet.Messager;
import me.jaimemartz.faucet.Replacement;
import me.jaimemartz.lobbybalancer.LobbyBalancer;
import me.jaimemartz.lobbybalancer.configuration.ConfigEntries;
import me.jaimemartz.lobbybalancer.manager.PlayerLocker;
import me.jaimemartz.lobbybalancer.ping.PingStatus;
import me.jaimemartz.lobbybalancer.section.ServerSection;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public abstract class ConnectionIntent {
    protected final LobbyBalancer plugin;
    protected final ProxiedPlayer player;
    protected final ServerSection section;

    public ConnectionIntent(LobbyBalancer plugin, ProxiedPlayer player, ProviderType provider, ServerSection section, List<ServerInfo> servers) {
        this.plugin = plugin;
        this.player = player;
        this.section = section;

        if (servers == section.getServers()) {
            throw new IllegalStateException("The servers list parameter is the same object as the section servers list, this cannot happen");
        }

        if (section.getProvider() != ProviderType.NONE) {
            ServerInfo target = this.fetchServer(plugin, player, section, provider, servers);
            if (target != null) {
                new Messager(player).send(ConfigEntries.CONNECTING_MESSAGE.get(), new Replacement("{server}", target.getName()));
                this.simple(target);
            } else {
                new Messager(player).send(ConfigEntries.FAILURE_MESSAGE.get());
                this.failure();
            }
        }
    }

    public ConnectionIntent(LobbyBalancer plugin, ProxiedPlayer player, ServerSection section) {
        this(plugin, player, section.getProvider(), section);
    }

    public ConnectionIntent(LobbyBalancer plugin, ProxiedPlayer player, ProviderType type, ServerSection section) {
        this(plugin, player, type, section, new ArrayList<>(section.getServers()));
    }

    public ConnectionIntent(LobbyBalancer plugin, ProxiedPlayer player, ServerSection section, List<ServerInfo> servers) {
        this(plugin, player, section.getProvider(), section, servers);
    }

    private ServerInfo fetchServer(LobbyBalancer plugin, ProxiedPlayer player, ServerSection section, ProviderType provider, List<ServerInfo> servers) {
        if (ConfigEntries.ASSIGN_TARGETS_ENABLED.get()) {
            if (ServerAssignRegistry.hasAssignedServer(player, section)) {
                ServerInfo target = ServerAssignRegistry.getAssignedServer(player, section);
                PingStatus status = plugin.getPingManager().getStatus(target);
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

            PingStatus status = plugin.getPingManager().getStatus(target);
            if (status.isAccessible()) {
                return target;
            } else {
                servers.remove(target);
            }
        }

        return null;
    }

    public abstract void simple(ServerInfo server);

    public void failure() {
        //Nothing to do
    }

    public static void simple(LobbyBalancer plugin, ProxiedPlayer player, ServerSection section) {
        new ConnectionIntent(plugin, player, section) {
            @Override
            public void simple(ServerInfo server) {
                direct(plugin, player, server);
            }
        };
    }

    public static void direct(LobbyBalancer plugin, ProxiedPlayer player, ServerInfo server) {
        PlayerLocker.lock(player);
        player.connect(server);
        plugin.getProxy().getScheduler().schedule(plugin, () -> {
            PlayerLocker.unlock(player);
        }, 5, TimeUnit.SECONDS);
    }
}