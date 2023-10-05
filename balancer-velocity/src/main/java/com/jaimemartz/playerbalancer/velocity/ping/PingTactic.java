package com.jaimemartz.playerbalancer.velocity.ping;

import com.jaimemartz.playerbalancer.velocity.PlayerBalancer;
import com.jaimemartz.playerbalancer.velocity.utils.ServerListPing;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerPing;

import java.io.IOException;
import java.util.function.Consumer;

public enum PingTactic {
    CUSTOM {
        @Override
        public void ping(RegisteredServer server, Consumer<ServerStatus> callback, PlayerBalancer plugin) {
            plugin.getProxyServer().getScheduler().buildTask(plugin, () -> {
                try {
                    ServerListPing utility = new ServerListPing();
                    ServerListPing.StatusResponse response = utility.ping(
                            server.getServerInfo().getAddress(),
                            plugin.getSettings().getFeaturesProps().getServerCheckerProps().getTimeout());
                    callback.accept(new ServerStatus(
                                    response.getDescription(),
                                    response.getPlayers().getOnline(),
                                    response.getPlayers().getMax()));
                } catch (IOException e) {
                    callback.accept(null);
                }
            }).schedule();
        }
    },

    GENERIC {
        @Override
        public void ping(RegisteredServer server, Consumer<ServerStatus> callback, PlayerBalancer plugin) {
            try {
                server.ping().whenComplete((ping, throwable) -> {
                    if (ping != null) {
                        //  Using deprecated method for bungee 1.8 compatibility
                        callback.accept(new ServerStatus(
                                ping.getDescriptionComponent(),
                                ping.getPlayers().map(ServerPing.Players::getOnline).orElse(0),
                                ping.getPlayers().map(ServerPing.Players::getMax).orElse(0)
                        ));
                    } else {
                        callback.accept(null);
                    }
                });
            } catch (Exception e) {
                callback.accept(null);
            }
        }
    };

    public abstract void ping(RegisteredServer server, Consumer<ServerStatus> callback, PlayerBalancer plugin);
}
