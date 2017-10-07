package com.jaimemartz.playerbalancer.connection.provider.types;

import com.jaimemartz.playerbalancer.PlayerBalancer;
import com.jaimemartz.playerbalancer.connection.provider.AbstractProvider;
import com.jaimemartz.playerbalancer.ping.ServerStatus;
import com.jaimemartz.playerbalancer.section.ServerSection;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class ProgressiveProvider extends AbstractProvider {
    @Override
    public ServerInfo requestTarget(PlayerBalancer plugin, ServerSection section, List<ServerInfo> servers, ProxiedPlayer player) {
        for (ServerInfo server : servers) {
            ServerStatus status = plugin.getStatusManager().getStatus(server);
            if (plugin.getNetworkManager().getPlayers(server) < status.getMaximum()) {
                return server;
            }
        }

        return servers.get(ThreadLocalRandom.current().nextInt(servers.size()));
    }
}
