package com.jaimemartz.playerbalancer.velocity.connection.provider.types.random;

import com.jaimemartz.playerbalancer.velocity.PlayerBalancer;
import com.jaimemartz.playerbalancer.velocity.connection.provider.AbstractProvider;
import com.jaimemartz.playerbalancer.velocity.section.ServerSection;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import java.util.ArrayList;
import java.util.List;

import static com.jaimemartz.playerbalancer.velocity.utils.RandomUtils.random;

public class RandomLowestProvider extends AbstractProvider {
    @Override
    public RegisteredServer requestTarget(PlayerBalancer plugin, ServerSection section, List<RegisteredServer> servers, Player player) {
        List<RegisteredServer> results = new ArrayList<>();
        int min = Integer.MAX_VALUE;

        for (RegisteredServer server : servers) {
            int count = plugin.getNetworkManager().getPlayers(server.getServerInfo());

            if (count <= min) {
                if (count < min) {
                    min = count;
                    results.clear();
                }
                results.add(server);
            }
        }

        return random(results);
    }
}