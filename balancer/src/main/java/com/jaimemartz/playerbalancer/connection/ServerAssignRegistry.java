package com.jaimemartz.playerbalancer.connection;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.jaimemartz.playerbalancer.section.ServerSection;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Map;

public class ServerAssignRegistry {
    private static final Table<ProxiedPlayer, ServerSection, ServerInfo> table = HashBasedTable.create();

    public static void assignTarget(ProxiedPlayer player, ServerSection section, ServerInfo server) {
        synchronized (table) {
            table.put(player, section, server);
        }
    }

    public static void revokeTarget(ProxiedPlayer player, ServerSection section) {
        synchronized (table) {
            table.remove(player, section);
        }
    }

    public static ServerInfo getAssignedServer(ProxiedPlayer player, ServerSection section) {
        synchronized (table) {
            return table.get(player, section);
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

    public static boolean hasAssignedServer(ProxiedPlayer player, ServerSection section) {
        synchronized (table) {
            return table.contains(player, section);
        }
    }

    public static Table<ProxiedPlayer, ServerSection, ServerInfo> getTable() {
        synchronized (table) {
            return table;
        }
    }
}
