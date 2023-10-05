package com.jaimemartz.playerbalancer.velocity.connection.provider.types.progressive;

import com.jaimemartz.playerbalancer.velocity.PlayerBalancer;
import com.jaimemartz.playerbalancer.velocity.connection.provider.AbstractProvider;
import com.jaimemartz.playerbalancer.velocity.ping.ServerStatus;
import com.jaimemartz.playerbalancer.velocity.section.ServerSection;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import java.util.List;

public class ProgressiveFillerProvider extends AbstractProvider {
    @Override
    public RegisteredServer requestTarget(PlayerBalancer plugin, ServerSection section, List<RegisteredServer> servers, Player player) {
        int max = Integer.MIN_VALUE;
        RegisteredServer target = null;

        for (RegisteredServer server : servers) {
            ServerStatus status = plugin.getStatusManager().getStatus(server.getServerInfo());
            int count = plugin.getNetworkManager().getPlayers(server.getServerInfo());

            if (count > max && count <= status.getMaximum()) {
                max = count;
                target = server;
            }
        }

        return target;
    }
}