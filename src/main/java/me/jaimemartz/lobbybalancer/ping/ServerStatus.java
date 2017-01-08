package me.jaimemartz.lobbybalancer.ping;

import me.jaimemartz.lobbybalancer.configuration.ConfigEntries;

public final class ServerStatus {
    private final String description;
    private final int online, maximum;
    private boolean accessible;

    public ServerStatus(String description, int online, int maximum) {
        this.description = description;
        this.online = online;
        this.maximum = maximum;
        boolean accessible = true;
        if (maximum != 0) {
            for (String pattern : ConfigEntries.SERVER_CHECK_MARKER_MOTDS.get()) {
                if (description.contains(pattern) || description.matches(pattern)) {
                    accessible = false;
                }
            }

            if (online >= maximum) {
                accessible = false;
            }
        } else {
            accessible = false;
        }
        this.accessible = accessible;
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

    public boolean isAccessible() {
        return accessible;
    }

    public void setAccessible(boolean accessible) {
        this.accessible = accessible;
    }
}