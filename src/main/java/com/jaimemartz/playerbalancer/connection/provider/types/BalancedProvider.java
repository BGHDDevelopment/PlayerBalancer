package com.jaimemartz.playerbalancer.connection.provider.types;

import com.jaimemartz.playerbalancer.PlayerBalancer;
import com.jaimemartz.playerbalancer.connection.provider.AbstractProvider;
import com.jaimemartz.playerbalancer.section.ServerSection;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class BalancedProvider extends AbstractProvider {
    @Override
    public ServerInfo requestTarget(PlayerBalancer plugin, ServerSection section, List<ServerInfo> servers, ProxiedPlayer player) {
        List<ServerInfo> results = new ArrayList<>();
        int min = Integer.MAX_VALUE;

        for (ServerInfo server : servers) {
            int count = plugin.getNetworkManager().getPlayers(server);

            if (count <= min) {
                if (count < min) {
                    min = count;
                    results.clear();
                }
                results.add(server);
            }
        }

        return results.get(ThreadLocalRandom.current().nextInt(results.size()));
    }
}