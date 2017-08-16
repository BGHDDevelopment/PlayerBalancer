package com.jaimemartz.playerbalancer.connection;

import com.jaimemartz.playerbalancer.PlayerBalancer;
import com.jaimemartz.playerbalancer.manager.NetworkManager;
import com.jaimemartz.playerbalancer.section.ServerSection;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public enum ProviderType {
    NONE {
        @Override
        public ServerInfo requestTarget(PlayerBalancer plugin, ServerSection section, List<ServerInfo> list, ProxiedPlayer player) {
            return null;
        }
    },

    LOWEST {
        @Override
        public ServerInfo requestTarget(PlayerBalancer plugin, ServerSection section, List<ServerInfo> list, ProxiedPlayer player) {
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

    RANDOM {
        @Override
        public ServerInfo requestTarget(PlayerBalancer plugin, ServerSection section, List<ServerInfo> list, ProxiedPlayer player) {
            return list.get(ThreadLocalRandom.current().nextInt(list.size()));
        }
    },

    PROGRESSIVE {
        @Override
        public ServerInfo requestTarget(PlayerBalancer plugin, ServerSection section, List<ServerInfo> list, ProxiedPlayer player) {
            /*
            for (ServerInfo server : list) {
                ServerStatus status = plugin.getStatusManager().getStatus(server);
                if (NetworkManager.getPlayers(server).size() < status.getMaximum()) {
                    return server;
                }
            }

            return list.get(ThreadLocalRandom.current().nextInt(list.size()));
            */
            return null;
        }
    },

    FILLER {
        @Override
        public ServerInfo requestTarget(PlayerBalancer plugin, ServerSection section, List<ServerInfo> list, ProxiedPlayer player) {
            /*
            int max = Integer.MIN_VALUE;
            ServerInfo target = null;

            for (ServerInfo server : list) {
                ServerStatus status = plugin.getStatusManager().getStatus(server);
                int count = NetworkManager.getPlayers(server).size();

                if (count > max && count <= status.getMaximum()) {
                    max = count;
                    target = server;
                }
            }

            return target;
            */
            return null;
        }
    };

    public abstract ServerInfo requestTarget(PlayerBalancer plugin, ServerSection section, List<ServerInfo> list, ProxiedPlayer player);
}