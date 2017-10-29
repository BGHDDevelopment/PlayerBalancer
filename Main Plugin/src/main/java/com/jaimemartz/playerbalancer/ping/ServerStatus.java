package com.jaimemartz.playerbalancer.ping;

import net.md_5.bungee.api.config.ServerInfo;

import java.util.Collections;
import java.util.List;

public final class ServerStatus {
    private final String description;
    private final int online, maximum;
    private boolean outdated = true;
    private final boolean accessible;

    public ServerStatus() {
        this("Server Unreachable", 0, 0, Collections.emptyList());
    }

    public ServerStatus(ServerInfo server) {
        this(server.getMotd(), server.getPlayers().size(), Integer.MAX_VALUE, Collections.emptyList());
    }

    public ServerStatus(String description, int online, int maximum, List<String> descs) {
        this.description = description;
        this.online = online;
        this.maximum = maximum;

        this.accessible = (maximum != 0 && online < maximum && !isMarked(descs));
    }

    private boolean isMarked(List<String> descs) {
        for (String pattern : descs) {
            if (description.matches(pattern)) {
                return true;
            }
        }
        return false;
    }

    public String getDescription() {
        return description;
    }

    public int getOnline() {
        return online;
    }

    public int getMaximum() {
        return maximum;
    }

    public boolean isOutdated() {
        return outdated;
    }

    public void setOutdated(boolean outdated) {
        this.outdated = outdated;
    }

    public boolean isAccessible() {
        return accessible;
    }
}