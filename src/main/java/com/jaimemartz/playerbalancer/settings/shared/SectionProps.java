package com.jaimemartz.playerbalancer.settings.shared;

import com.jaimemartz.playerbalancer.connection.ProviderType;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.List;

@ConfigSerializable
public class SectionProps {
    @Setting
    private boolean principal; //TODO move this to other place

    @Setting
    private boolean dummy; //TODO move this to other place

    @Setting
    private ProviderType provider;

    @Setting("parent")
    private String parentName;

    @Setting("servers")
    private List<String> serverEntries;

    @Setting
    private CommandProps command;

    @Setting("server")
    private String serverName;

    public boolean isPrincipal() {
        return principal;
    }

    public void setPrincipal(boolean principal) {
        this.principal = principal;
    }

    public boolean isDummy() {
        return dummy;
    }

    public void setDummy(boolean dummy) {
        this.dummy = dummy;
    }

    public ProviderType getProvider() {
        return provider;
    }

    public void setProvider(ProviderType provider) {
        this.provider = provider;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public List<String> getServerEntries() {
        return serverEntries;
    }

    public void setServerEntries(List<String> serverEntries) {
        this.serverEntries = serverEntries;
    }

    public CommandProps getCommand() {
        return command;
    }

    public void setCommand(CommandProps command) {
        this.command = command;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    @Override
    public String toString() {
        return "SectionProps{" +
                "principal=" + principal +
                ", dummy=" + dummy +
                ", provider=" + provider +
                ", parentName='" + parentName + '\'' +
                ", serverEntries=" + serverEntries +
                ", command=" + command +
                ", serverName='" + serverName + '\'' +
                '}';
    }
}
