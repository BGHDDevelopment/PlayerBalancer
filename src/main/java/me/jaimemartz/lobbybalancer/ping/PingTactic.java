package me.jaimemartz.lobbybalancer.ping;

import me.jaimemartz.faucet.ServerListPing;
import me.jaimemartz.faucet.StatusResponse;
import me.jaimemartz.lobbybalancer.LobbyBalancer;
import me.jaimemartz.lobbybalancer.configuration.ConfigEntries;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.config.ServerInfo;

import java.io.IOException;

public enum PingTactic {
    CUSTOM {
        ServerListPing utility = new ServerListPing();

        @Override
        public void ping(ServerInfo server, Callback<StatusInfo> callback, LobbyBalancer plugin) {
            utility.setTimeout(ConfigEntries.SERVER_CHECK_TIMEOUT.get());
            plugin.getProxy().getScheduler().runAsync(plugin, () -> {
                try {
                    StatusResponse response = utility.ping(server.getAddress());
                    callback.done(new StatusInfo(
                            response.getDescription().toLegacyText(),
                            response.getPlayers().getOnline(),
                            response.getPlayers().getMax()),
                            null);
                } catch (IOException e) {
                    callback.done(null, e);
                }
            });
        }
    },

    GENERIC {
        @Override
        public void ping(ServerInfo server, Callback<StatusInfo> callback, LobbyBalancer plugin) {
            try {
                server.ping((ping, throwable) -> {
                    if (ping != null) {
                        callback.done(new StatusInfo(
                                ping.getDescription(),
                                ping.getPlayers().getOnline(),
                                ping.getPlayers().getMax()
                        ), throwable);
                    } else {
                        callback.done(null, throwable);
                    }
                });
            } catch (Exception e) {
                callback.done(null, e);
            }
        }
    };

    public abstract void ping(ServerInfo server, Callback<StatusInfo> callback, LobbyBalancer plugin);
}
