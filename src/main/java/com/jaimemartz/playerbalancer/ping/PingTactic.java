package com.jaimemartz.playerbalancer.ping;

import com.jaimemartz.playerbalancer.utils.ServerListPing;
import com.jaimemartz.playerbalancer.utils.ServerListPing.StatusResponse;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.IOException;

public enum PingTactic {
    CUSTOM {
        ServerListPing utility = new ServerListPing();

        @Override
        public void ping(ServerInfo server, Callback<ServerStatus> callback, Plugin plugin) {
            plugin.getProxy().getScheduler().runAsync(plugin, () -> {
                try {
                    StatusResponse response = utility.ping(server.getAddress());
                    callback.done(new ServerStatus(
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
        public void ping(ServerInfo server, Callback<ServerStatus> callback, Plugin plugin) {
            try {
                server.ping((ping, throwable) -> {
                    if (ping != null) {
                        //using deprecated method for 1.8 compatibility
                        callback.done(new ServerStatus(
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

    public abstract void ping(ServerInfo server, Callback<ServerStatus> callback, Plugin plugin);
}
