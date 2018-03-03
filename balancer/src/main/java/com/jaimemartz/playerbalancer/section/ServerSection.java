package com.jaimemartz.playerbalancer.section;

import com.jaimemartz.playerbalancer.connection.ProviderType;
import com.jaimemartz.playerbalancer.connection.provider.AbstractProvider;
import com.jaimemartz.playerbalancer.settings.props.shared.SectionProps;
import com.jaimemartz.playerbalancer.utils.AlphanumComparator;
import lombok.Data;
import net.md_5.bungee.api.config.ServerInfo;

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

@Data
public class ServerSection {
    private final String name;
    private final SectionProps props;

    private boolean inherited = false;
    private ServerSection parent;
    private int position;

    private transient ServerInfo server;
    private transient SectionCommand command;
    private transient AbstractProvider externalProvider;
    private Set<ServerInfo> servers;

    private boolean valid = false;

    public ServerSection(String name, SectionProps props) {
        this.name = name;
        this.props = props;

        this.servers = Collections.synchronizedNavigableSet(new TreeSet<>((lhs, rhs) ->
                AlphanumComparator.getInstance().compare(lhs.getName(), rhs.getName())
        ));
    }

    public ProviderType getImplicitProvider() {
        if (inherited) {
            return parent.getImplicitProvider();
        } else {
            return props.getProvider();
        }
    }
}