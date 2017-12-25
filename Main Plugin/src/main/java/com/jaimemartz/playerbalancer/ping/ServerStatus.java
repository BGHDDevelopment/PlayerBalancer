package com.jaimemartz.playerbalancer.ping;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.config.ServerInfo;

public class ServerStatus {
    private final String description;
    private final int players, maximum;
    private final boolean online;
    private final boolean fabricated;
    private boolean outdated = true;

    /**
     * Constructor when cannot ping the server
     */
    public ServerStatus() {
        this.description = "Server Unreachable";
        this.players = 0;
        this.maximum = 0;
        this.online = false;
        this.fabricated = true;
    }

    /**
     * Constructor when we have to return defaults
     * Defaulting to be accessible as this is used when the server checker is disabled
     * @param server the server for providing basic info about itself
     */
    public ServerStatus(ServerInfo server) {
        this.description = revertColor(server.getMotd());
        this.players = server.getPlayers().size();
        this.maximum = Integer.MAX_VALUE;
        this.online = true;
        this.fabricated = true;
    }

    /**
     * Constructor when we have to store ping results
     * @param description the description (aka MOTD) from the ping result
     * @param players the count of players online from the ping result
     * @param maximum the maximum amount of players possible from the ping result
     */
    public ServerStatus(String description, int players, int maximum) {
        this.description = revertColor(description);
        this.players = players;
        this.maximum = maximum;
        this.online = maximum != 0 && players < maximum;
        this.fabricated = false;
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

    public boolean isOnline() {
        return online;
    }

    public boolean isFabricated() {
        return fabricated;
    }

    public boolean isOutdated() {
        return outdated;
    }

    public void setOutdated(boolean outdated) {
        this.outdated = outdated;
    }

    private static String revertColor(String string) {
        return string.replace(ChatColor.COLOR_CHAR, '&');
    }
}