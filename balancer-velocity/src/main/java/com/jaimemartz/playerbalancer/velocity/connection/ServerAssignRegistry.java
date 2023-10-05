package com.jaimemartz.playerbalancer.velocity.connection;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.jaimemartz.playerbalancer.velocity.section.ServerSection;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.ServerInfo;

import java.util.Map;

public class ServerAssignRegistry {
    private static final Table<Player, ServerSection, ServerInfo> table = HashBasedTable.create();

    public static void assignTarget(Player player, ServerSection section, ServerInfo server) {
        synchronized (table) {
            table.put(player, section, server);
        }
    }

    public static void revokeTarget(Player player, ServerSection section) {
        synchronized (table) {
            table.remove(player, section);
        }
    }

    public static ServerInfo getAssignedServer(Player player, ServerSection section) {
        synchronized (table) {
            return table.get(player, section);
        }
    }

    public static Map<ServerSection, ServerInfo> getAssignments(Player player) {
        synchronized (table) {
            return table.row(player);
        }
    }

    public static void clearAsssignedServers(Player player) {
        synchronized (table) {
            table.row(player).clear();
        }
    }

    public static boolean hasAssignedServer(Player player, ServerSection section) {
        synchronized (table) {
            return table.contains(player, section);
        }
    }

    public static Table<Player, ServerSection, ServerInfo> getTable() {
        synchronized (table) {
            return table;
        }
    }
}
