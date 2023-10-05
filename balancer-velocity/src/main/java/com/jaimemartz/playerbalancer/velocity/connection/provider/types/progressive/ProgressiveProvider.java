package com.jaimemartz.playerbalancer.velocity.connection.provider.types.progressive;

import com.jaimemartz.playerbalancer.velocity.PlayerBalancer;
import com.jaimemartz.playerbalancer.velocity.connection.provider.AbstractProvider;
import com.jaimemartz.playerbalancer.velocity.ping.ServerStatus;
import com.jaimemartz.playerbalancer.velocity.section.ServerSection;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import java.util.List;

public class ProgressiveProvider extends AbstractProvider {
    @Override
    public RegisteredServer requestTarget(PlayerBalancer plugin, ServerSection section, List<RegisteredServer> servers, Player player) {
        for (RegisteredServer server : servers) {
            ServerStatus status = plugin.getStatusManager().getStatus(server.getServerInfo());
            if (plugin.getNetworkManager().getPlayers(server.getServerInfo()) < status.getMaximum()) {
                return server;
            }
        }

        return null;
    }
}
