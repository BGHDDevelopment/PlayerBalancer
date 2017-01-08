package me.jaimemartz.lobbybalancer.ping;

import me.jaimemartz.faucet.ServerListPing;
import me.jaimemartz.faucet.StatusResponse;
import me.jaimemartz.lobbybalancer.LobbyBalancer;
import net.md_5.bungee.api.config.ServerInfo;

import java.io.IOException;

public enum PingTacticType {
    CUSTOM {
        ServerListPing utility = new ServerListPing();

        @Override
        public void ping(ServerInfo server, PingCallback callback, LobbyBalancer plugin) {
            plugin.getProxy().getScheduler().runAsync(plugin, () -> {
                try {
                    StatusResponse response = utility.ping(server.getAddress());
                    callback.onPong(new ServerStatus(
                            response.getDescription().toLegacyText(),
                            response.getPlayers().getOnline(),
                            response.getPlayers().getMax()
                    ));
                } catch (IOException e) {
                    callback.onPong(new ServerStatus("Server Unreachable", 0, 0));
                }
            });
        }
    },

    GENERIC {
        @Override
        public void ping(ServerInfo server, PingCallback callback, LobbyBalancer plugin) {
            try {
                server.ping((ping, throwable) -> {
                    if (ping != null && throwable == null) {
                        callback.onPong(new ServerStatus(
                                ping.getDescription(),
                                ping.getPlayers().getOnline(),
                                ping.getPlayers().getMax()
                        ));
                    } else {
                        callback.onPong(new ServerStatus("Server Unreachable", 0, 0));
                    }
                });
            } catch (Exception e) {
                callback.onPong(new ServerStatus("Server Unreachable", 0, 0));
            }
        }
    };

    public abstract void ping(ServerInfo server, PingCallback callback, LobbyBalancer plugin);
}
