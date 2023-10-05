package com.jaimemartz.playerbalancer.velocity.helper;

import com.imaginarycode.minecraft.redisbungee.RedisBungeeAPI;
import com.jaimemartz.playerbalancer.velocity.PlayerBalancer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;

import java.util.Optional;

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

        Optional<RegisteredServer> serverConnection = plugin.getProxyServer().getServer(server.getName());

        return serverConnection.map(registeredServer -> registeredServer.getPlayersConnected().size()).orElse(0);
    }
}
