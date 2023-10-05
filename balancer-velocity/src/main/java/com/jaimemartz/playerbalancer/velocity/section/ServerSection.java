package com.jaimemartz.playerbalancer.velocity.section;

import com.jaimemartz.playerbalancer.velocity.connection.ProviderType;
import com.jaimemartz.playerbalancer.velocity.connection.provider.AbstractProvider;
import com.jaimemartz.playerbalancer.velocity.settings.props.shared.SectionProps;
import com.jaimemartz.playerbalancer.velocity.utils.AlphanumComparator;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import lombok.Data;

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

    private transient RegisteredServer server;
    private transient SectionCommand command;
    private transient AbstractProvider externalProvider;
    private Set<RegisteredServer> servers;

    private boolean valid = false;

    public ServerSection(String name, SectionProps props) {
        this.name = name;
        this.props = props;

        this.servers = Collections.synchronizedNavigableSet(new TreeSet<>((lhs, rhs) ->
                AlphanumComparator.getInstance().compare(lhs.getServerInfo().getName(), rhs.getServerInfo().getName())
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