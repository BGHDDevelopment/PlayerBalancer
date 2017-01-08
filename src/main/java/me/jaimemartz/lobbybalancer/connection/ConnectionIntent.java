package me.jaimemartz.lobbybalancer.connection;

import me.jaimemartz.faucet.Messager;
import me.jaimemartz.lobbybalancer.LobbyBalancer;
import me.jaimemartz.lobbybalancer.configuration.ConfigEntries;
import me.jaimemartz.lobbybalancer.ping.ServerStatus;
import me.jaimemartz.lobbybalancer.section.ServerSection;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.List;

public abstract class ConnectionIntent {
    public ConnectionIntent(LobbyBalancer plugin, ProxiedPlayer player, ServerSection section) {
        ServerInfo target = this.findTarget(plugin, player, section);
        Messager msgr = new Messager(player);

        if (target != null) {
            msgr.send((ConfigEntries.CONNECTING_MESSAGE.get()).replace("{server}", target.getName()));
            this.connect(target);
        } else {
            msgr.send(ConfigEntries.FAILURE_MESSAGE.get());
            this.failure();
        }
    }

    private ServerInfo findTarget(LobbyBalancer plugin, ProxiedPlayer player, ServerSection section) {
        if (ConfigEntries.ASSIGN_TARGETS_ENABLED.get()) {
            if (ServerAssignRegistry.hasAssignedServer(player, section)) {
                ServerInfo target = ServerAssignRegistry.getAssignedServer(player, section);
                ServerStatus status = plugin.getPingManager().getStatus(target);
                if (status.isAccessible()) {
                    return target;
                } else {
                    ServerAssignRegistry.revokeTarget(player, section);
                }
            }
        }

        ProviderType provider = section.getProvider();
        int intents = ConfigEntries.SERVER_CHECK_ATTEMPTS.get();
        List<ServerInfo> servers = new ArrayList<>();
        servers.addAll(section.getServers());

        while (intents-- >= 1) {
            ServerInfo target = provider.requestTarget(plugin, section, servers, player);
            if (target == null) continue;

            if (servers.size() == 0) return null;
            if (servers.size() == 1) return servers.get(0);

            ServerStatus status = plugin.getPingManager().getStatus(target);
            if (status.isAccessible()) {
                return target;
            } else {
                servers.remove(target);
            }
        }
        return null;
    }

    public abstract void connect(ServerInfo server);

    public void failure() {
        //Nothing to do
    }
}
