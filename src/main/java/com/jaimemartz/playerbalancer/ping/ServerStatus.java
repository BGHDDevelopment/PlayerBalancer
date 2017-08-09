package com.jaimemartz.playerbalancer.ping;

import com.jaimemartz.playerbalancer.configuration.ConfigEntries;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.config.ServerInfo;

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

    //TODO improve this
    public boolean isAccessible() {
        if (maximum == 0) {
            return false;
        }

        for (String pattern : ConfigEntries.SERVER_CHECK_MARKER_DESCS.get()) {
            if (description.matches(pattern) || description.contains(pattern)) {
                return false;
            }
        }

        return online < maximum;
    }
}