package com.jaimemartz.playerbalancer.connection;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.jaimemartz.playerbalancer.section.ServerSection;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Map;

public class ServerAssignRegistry {
    private static final Table<ProxiedPlayer, ServerSection, ServerInfo> table = HashBasedTable.create();

    public static void assignTarget(ProxiedPlayer player, ServerSection group, ServerInfo server) {
        synchronized (table) {
            table.put(player, group, server);
        }
    }

    public static void revokeTarget(ProxiedPlayer player, ServerSection group) {
        synchronized (table) {
            table.remove(player, group);
        }
    }

    public static ServerInfo getAssignedServer(ProxiedPlayer player, ServerSection group) {
        synchronized (table) {
            return table.get(player, group);
        }
    }

    public static Map<ServerSection, ServerInfo> getAssignments(ProxiedPlayer player) {
        synchronized (table) {
            return table.row(player);
        }
    }

    public static void clearAsssignedServers(ProxiedPlayer player) {
        synchronized (table) {
            table.row(player).clear();
        }
    }

    public static boolean hasAssignedServer(ProxiedPlayer player, ServerSection group) {
        synchronized (table) {
            return table.contains(player, group);
        }
    }

    public static Table<ProxiedPlayer, ServerSection, ServerInfo> getTable() {
        synchronized (table) {
            return table;
        }
    }
}
