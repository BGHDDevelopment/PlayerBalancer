package com.jaimemartz.playerbalancer.connection.provider.types;

import com.jaimemartz.playerbalancer.PlayerBalancer;
import com.jaimemartz.playerbalancer.connection.provider.AbstractProvider;
import com.jaimemartz.playerbalancer.section.ServerSection;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.List;

public class LowestProvider extends AbstractProvider {
    @Override
    public ServerInfo requestTarget(PlayerBalancer plugin, ServerSection section, List<ServerInfo> servers, ProxiedPlayer player) {
        int min = Integer.MAX_VALUE;
        ServerInfo target = null;

        for (ServerInfo server : servers) {
            int count = plugin.getNetworkManager().getPlayers(server);

            if (count < min) {
                min = count;
                target = server;
            }
        }

        return target;
    }
}