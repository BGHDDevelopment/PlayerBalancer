package com.jaimemartz.playerbalancer.section;

import com.jaimemartz.playerbalancer.connection.ProviderType;
import com.jaimemartz.playerbalancer.settings.props.shared.SectionProps;
import net.md_5.bungee.api.config.ServerInfo;

import java.util.List;

public class ServerSection {
    private final String name;
    private final SectionProps props;

    private boolean inherited = false;
    private ServerSection parent;
    private int position;

    private ServerInfo server;
    private SectionCommand command;

    private List<ServerInfo> mappedServers;
    private List<ServerInfo> sortedServers;

    private boolean valid = false;

    public ServerSection(String name, SectionProps props) {
        this.name = name;
        this.props = props;
    }

    public String getName() {
        return name;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public SectionProps getProps() {
        return props;
    }

    public boolean isInherited() {
        return inherited;
    }

    public void setInherited(boolean inherited) {
        this.inherited = inherited;
    }

    public ServerSection getParent() {
        return parent;
    }

    public void setParent(ServerSection parent) {
        this.parent = parent;
    }

    public ProviderType getEffectiveProvider() {
        return inherited ? parent.getEffectiveProvider() : props.getProvider();
    }

    public void setProvider(ProviderType provider) {
        props.setProvider(provider);
        inherited = false;
    }

    public ServerInfo getServer() {
        return server;
    }

    public void setServer(ServerInfo server) {
        this.server = server;
    }

    public SectionCommand getCommand() {
        return command;
    }

    public void setCommand(SectionCommand command) {
        this.command = command;
    }

    public List<ServerInfo> getMappedServers() {
        return mappedServers;
    }

    public void setMappedServers(List<ServerInfo> mappedServers) {
        this.mappedServers = mappedServers;
    }

    public List<ServerInfo> getSortedServers() {
        return sortedServers;
    }

    public void setSortedServers(List<ServerInfo> sortedServers) {
        this.sortedServers = sortedServers;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    @Override
    public String toString() {
        return "ServerSection{" +
                "name='" + name + '\'' +
                ", props=" + props +
                ", inherited=" + inherited +
                ", parent=" + parent +
                ", position=" + position +
                ", server=" + server +
                ", command=" + command +
                ", mappedServers=" + mappedServers +
                ", sortedServers=" + sortedServers +
                ", valid=" + valid +
                '}';
    }
}