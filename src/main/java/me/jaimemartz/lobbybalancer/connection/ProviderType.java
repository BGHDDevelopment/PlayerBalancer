package me.jaimemartz.lobbybalancer.connection;

import com.google.common.collect.Iterables;
import me.jaimemartz.lobbybalancer.LobbyBalancer;
import me.jaimemartz.lobbybalancer.manager.NetworkManager;
import me.jaimemartz.lobbybalancer.ping.PingStatus;
import me.jaimemartz.lobbybalancer.section.ServerSection;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public enum ProviderType {
    NONE(0, "Returns no server") {
        @Override
        public ServerInfo requestTarget(LobbyBalancer plugin, ServerSection section, List<ServerInfo> list, ProxiedPlayer player) {
            return null;
        }
    },

    DIRECT(1, "Returns the only server in the list") {
        @Override
        public ServerInfo requestTarget(LobbyBalancer plugin, ServerSection section, List<ServerInfo> list, ProxiedPlayer player) {
            return Iterables.getOnlyElement(list);
        }
    },

    LOWEST(3, "Returns the server with the least players online") {
        @Override
        public ServerInfo requestTarget(LobbyBalancer plugin, ServerSection section, List<ServerInfo> list, ProxiedPlayer player) {
            int min = Integer.MAX_VALUE;
            ServerInfo target = null;

            for (ServerInfo server : list) {
                int count = NetworkManager.getPlayers(server).size();

                if (count < min) {
                    min = count;
                    target = server;
                }
            }

            return target;
        }
    },

    RANDOM(4, "Returns a random server") {
        @Override
        public ServerInfo requestTarget(LobbyBalancer plugin, ServerSection section, List<ServerInfo> list, ProxiedPlayer player) {
            return list.get(ThreadLocalRandom.current().nextInt(list.size()));
        }
    },

    PROGRESSIVE(5, "Returns the first server that is not full") {
        @Override
        public ServerInfo requestTarget(LobbyBalancer plugin, ServerSection section, List<ServerInfo> list, ProxiedPlayer player) {
            for (ServerInfo server : list) {
                PingStatus status = plugin.getPingManager().getStatus(server);
                if (NetworkManager.getPlayers(server).size() < status.getMaximumPlayers()) {
                    return server;
                }
            }

            return list.get(ThreadLocalRandom.current().nextInt(list.size()));
        }
    },

    FILLER(6, "Returns the server with the most players online that is not full") {
        @Override
        public ServerInfo requestTarget(LobbyBalancer plugin, ServerSection section, List<ServerInfo> list, ProxiedPlayer player) {
            int max = Integer.MIN_VALUE;
            ServerInfo target = null;

            for (ServerInfo server : list) {
                PingStatus status = plugin.getPingManager().getStatus(server);
                int count = NetworkManager.getPlayers(server).size();

                if (count > max && count <= status.getMaximumPlayers()) {
                    max = count;
                    target = server;
                }
            }

            return target;
        }
    };

    private final int id;
    private final String description;

    ProviderType(int id, String description) {
        this.id = id;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public abstract ServerInfo requestTarget(LobbyBalancer plugin, ServerSection section, List<ServerInfo> list, ProxiedPlayer player);
}