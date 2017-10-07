package com.jaimemartz.playerbalancer.connection.provider.types;

import com.jaimemartz.playerbalancer.PlayerBalancer;
import com.jaimemartz.playerbalancer.connection.provider.AbstractProvider;
import com.jaimemartz.playerbalancer.ping.ServerStatus;
import com.jaimemartz.playerbalancer.section.ServerSection;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.List;

public class FillerProvider extends AbstractProvider {
    @Override
    public ServerInfo requestTarget(PlayerBalancer plugin, ServerSection section, List<ServerInfo> servers, ProxiedPlayer player) {
        int max = Integer.MIN_VALUE;
        ServerInfo target = null;

        for (ServerInfo server : servers) {
            ServerStatus status = plugin.getStatusManager().getStatus(server);
            int count = plugin.getNetworkManager().getPlayers(server);

            if (count > max && count <= status.getMaximum()) {
                max = count;
                target = server;
            }
        }

        return target;
    }
}