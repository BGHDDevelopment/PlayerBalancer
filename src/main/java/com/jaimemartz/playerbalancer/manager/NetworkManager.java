package com.jaimemartz.playerbalancer.manager;

import com.imaginarycode.minecraft.redisbungee.RedisBungee;
import com.jaimemartz.playerbalancer.configuration.ConfigEntries;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class NetworkManager {
    public static Set<UUID> getPlayers(ServerInfo server) {
        if (ConfigEntries.REDIS_BUNGEE_ENABLED.get()) {
            try {
                return RedisBungee.getApi().getPlayersOnServer(server.getName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return server.getPlayers().stream().map(ProxiedPlayer::getUniqueId).collect(Collectors.toSet());
    }
}
