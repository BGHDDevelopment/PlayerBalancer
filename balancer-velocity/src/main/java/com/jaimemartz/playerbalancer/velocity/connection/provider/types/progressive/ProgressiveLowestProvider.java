package com.jaimemartz.playerbalancer.velocity.connection.provider.types.progressive;

import com.jaimemartz.playerbalancer.velocity.PlayerBalancer;
import com.jaimemartz.playerbalancer.velocity.connection.provider.AbstractProvider;
import com.jaimemartz.playerbalancer.velocity.section.ServerSection;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import java.util.List;

public class ProgressiveLowestProvider extends AbstractProvider {
    @Override
    public RegisteredServer requestTarget(PlayerBalancer plugin, ServerSection section, List<RegisteredServer> servers, Player player) {
        int min = Integer.MAX_VALUE;
        RegisteredServer target = null;

        for (RegisteredServer server : servers) {
            int count = plugin.getNetworkManager().getPlayers(server.getServerInfo());

            if (count < min) {
                min = count;
                target = server;
            }
        }

        return target;
    }
}