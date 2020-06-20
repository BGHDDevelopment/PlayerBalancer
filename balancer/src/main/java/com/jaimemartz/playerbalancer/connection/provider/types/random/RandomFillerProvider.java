package com.jaimemartz.playerbalancer.connection.provider.types.random;

import com.jaimemartz.playerbalancer.PlayerBalancer;
import com.jaimemartz.playerbalancer.connection.provider.AbstractProvider;
import com.jaimemartz.playerbalancer.section.ServerSection;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.List;

import static com.jaimemartz.playerbalancer.utils.RandomUtils.random;

public class RandomFillerProvider extends AbstractProvider {
    @Override
    public ServerInfo requestTarget(PlayerBalancer plugin, ServerSection section, List<ServerInfo> servers, ProxiedPlayer player) {
        List<ServerInfo> results = new ArrayList<>();
        int max = Integer.MIN_VALUE;

        for (ServerInfo server : servers) {
            int count = server.getPlayers().size();

            if (count >= max) {
                if (count > max) {
                    max = count;
                    results.clear();
                }

                results.add(server);
            }
        }

        return random(results);
    }
}