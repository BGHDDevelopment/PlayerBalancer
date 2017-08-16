package com.jaimemartz.playerbalancer.settings.beans;

import com.jaimemartz.playerbalancer.connection.ProviderType;

import java.util.List;

public class SectionData {
    private Boolean principal = true;
    private Boolean dummy;
    private String parent;
    private List<String> servers;
    private ProviderType provider;
    private CommandData command;
    private String server;

    public Boolean isPrincipal() {
        return principal;
    }

    public void setPrincipal(boolean principal) {
        this.principal = principal;
    }

    public Boolean isDummy() {
        return dummy;
    }

    public void setDummy(boolean dummy) {
        this.dummy = dummy;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public List<String> getServers() {
        return servers;
    }

    public void setServers(List<String> servers) {
        this.servers = servers;
    }

    public ProviderType getProvider() {
        return provider;
    }

    public void setProvider(ProviderType provider) {
        this.provider = provider;
    }

    public CommandData getCommand() {
        return command;
    }

    public void setCommand(CommandData command) {
        this.command = command;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }
}
