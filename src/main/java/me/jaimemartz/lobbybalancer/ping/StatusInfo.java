package me.jaimemartz.lobbybalancer.ping;

import lombok.Getter;
import lombok.Setter;
import me.jaimemartz.lobbybalancer.configuration.ConfigEntries;
import net.md_5.bungee.api.config.ServerInfo;

public final class StatusInfo {
    @Getter private final String description;
    @Getter private final int online, maximum;
    @Getter @Setter private boolean outdated = true;

    public StatusInfo() {
        this("Server Unreachable", 0, 0);
    }

    public StatusInfo(ServerInfo server) {
        this(server.getMotd(), server.getPlayers().size(), Integer.MAX_VALUE);
    }

    public StatusInfo(String description, int online, int maximum) {
        this.description = description;
        this.online = online;
        this.maximum = maximum;
    }

    public boolean isAccessible() {
        if (maximum == 0) {
            return false;
        }

        for (String pattern : ConfigEntries.SERVER_CHECK_MARKER_DESCS.get()) {
            if (description.matches(pattern)) {
                return false;
            }
        }

        return online < maximum;
    }
}