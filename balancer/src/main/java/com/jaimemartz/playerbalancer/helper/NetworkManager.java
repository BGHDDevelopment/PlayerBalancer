package com.jaimemartz.playerbalancer.helper;

import com.imaginarycode.minecraft.redisbungee.RedisBungeeAPI;
import com.jaimemartz.playerbalancer.PlayerBalancer;
import net.md_5.bungee.api.config.ServerInfo;

public class NetworkManager {

    private final PlayerBalancer plugin;

    public NetworkManager(PlayerBalancer plugin) {
        this.plugin = plugin;
    }

    public int getPlayers(ServerInfo server) {
        if (plugin.getSettings().getGeneralProps().isRedisBungee()) {
            try {
                return RedisBungeeAPI.getRedisBungeeApi().getPlayersOnServer(server.getName()).size();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return server.getPlayers().size();
    }
}
