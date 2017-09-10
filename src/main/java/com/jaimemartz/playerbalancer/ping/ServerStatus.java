package com.jaimemartz.playerbalancer.ping;

import com.jaimemartz.playerbalancer.PlayerBalancer;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public final class ServerStatus {
    @Getter private final String description;
    @Getter private final int online, maximum;
    @Getter @Setter private boolean outdated = true;

    public ServerStatus() {
        this("Server Unreachable", 0, 0);
    }

    public ServerStatus(ServerInfo server) {
        this(server.getMotd(), server.getPlayers().size(), Integer.MAX_VALUE);
    }

    public ServerStatus(String description, int online, int maximum) {
        this.description = description;
        this.online = online;
        this.maximum = maximum;
    }

    //TODO improve this (set from the pinger if accessible or not)
    public boolean isAccessible(PlayerBalancer plugin, ProxiedPlayer player) {
        if (maximum == 0) {
            return false;
        }

        for (String pattern : plugin.getSettings().getServerCheckerProps().getMarkerDescs()) {
            if (description.matches(pattern) || description.contains(pattern)) {
                return false;
            }
        }

        return online < maximum;
    }
}