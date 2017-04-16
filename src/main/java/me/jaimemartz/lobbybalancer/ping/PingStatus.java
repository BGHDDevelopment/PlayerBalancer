package me.jaimemartz.lobbybalancer.ping;

import me.jaimemartz.lobbybalancer.configuration.ConfigEntries;
import net.md_5.bungee.api.config.ServerInfo;

public final class PingStatus {
    private final String description;
    private final int online, maximum;
    private boolean outdated = true;

    public PingStatus() {
        this("Server Unreachable", 0, 0);
    }

    public PingStatus(ServerInfo server) {
        this(server.getMotd(), server.getPlayers().size(), Integer.MAX_VALUE);
    }

    public PingStatus(String description, int online, int maximum) {
        this.description = description;
        this.online = online;
        this.maximum = maximum;
    }

    public String getDescription() {
        return description;
    }

    public int getOnlinePlayers() {
        return online;
    }

    public int getMaximumPlayers() {
        return maximum;
    }

    public void setOutdated(boolean outdated) {
        this.outdated = outdated;
    }

    public boolean isOutdated() {
        return outdated;
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