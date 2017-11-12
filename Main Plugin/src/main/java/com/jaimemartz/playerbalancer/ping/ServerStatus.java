package com.jaimemartz.playerbalancer.ping;

import net.md_5.bungee.api.config.ServerInfo;

public final class ServerStatus {
    private final String description;
    private final int players, maximum;
    private boolean outdated = true;
    private final boolean online;

    public ServerStatus() {
        this("Server Unreachable", 0, 0);
    }

    public ServerStatus(ServerInfo server) {
        this(server.getMotd(), server.getPlayers().size(), Integer.MAX_VALUE);
    }

    public ServerStatus(String description, int players, int maximum) {
        this.description = description;
        this.players = players;
        this.maximum = maximum;
        this.online = maximum != 0 && players < maximum;
    }

    public String getDescription() {
        return description;
    }

    public int getPlayers() {
        return players;
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

    public boolean isOnline() {
        return online;
    }
}