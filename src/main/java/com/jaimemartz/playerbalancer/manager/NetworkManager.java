package com.jaimemartz.playerbalancer.manager;

import net.md_5.bungee.api.config.ServerInfo;

import java.util.Set;
import java.util.UUID;

public class NetworkManager {
    public static Set<UUID> getPlayers(ServerInfo server) {
        /*
        if (settings.getProperty(GeneralProperties.REDIS_BUNGEE)) { //TODO false for now
            try {
                return RedisBungee.getApi().getPlayersOnServer(server.getName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return server.getPlayers().stream().map(ProxiedPlayer::getUniqueId).collect(Collectors.toSet());
        */

        return null;
    }
}
