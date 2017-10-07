package com.jaimemartz.playerbalancer.settings.props.shared;

import com.jaimemartz.playerbalancer.connection.ProviderType;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.List;

@ConfigSerializable
public class SectionProps {
    @Setting
    private ProviderType provider;

    @Setting(value = "parent")
    private String parentName;

    @Setting(value = "servers")
    private List<String> serverEntries;

    @Setting(value = "section-command")
    private CommandProps commandProps;

    @Setting(value = "section-server")
    private String serverName;

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

    public CommandProps getCommandProps() {
        return commandProps;
    }

    public void setCommandProps(CommandProps commandProps) {
        this.commandProps = commandProps;
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
                "provider=" + provider +
                ", parentName='" + parentName + '\'' +
                ", serverEntries=" + serverEntries +
                ", commandProps=" + commandProps +
                ", serverName='" + serverName + '\'' +
                '}';
    }
}
