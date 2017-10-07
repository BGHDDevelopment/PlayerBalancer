package com.jaimemartz.playerbalancer.ping;

import com.jaimemartz.playerbalancer.PlayerBalancer;
import net.md_5.bungee.api.config.ServerInfo;

public final class ServerStatus {
    private final String description;
    private final int online, maximum;
    private boolean outdated = true;

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

    public boolean isAccessible(PlayerBalancer plugin) {
        if (maximum == 0) {
            return false;
        }

        for (String pattern : plugin.getSettings().getServerCheckerProps().getMarkerDescs()) {
            if (description.matches(pattern)) {
                return false;
            }
        }

        return online < maximum;
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
}