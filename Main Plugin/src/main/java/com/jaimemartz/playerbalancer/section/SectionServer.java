package com.jaimemartz.playerbalancer.section;

import com.google.common.collect.Iterables;
import com.jaimemartz.playerbalancer.settings.props.features.BalancerProps;
import net.md_5.bungee.BungeeServerInfo;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SectionServer extends BungeeServerInfo {
    private final BalancerProps props;
    private final ServerSection section;

    public SectionServer(BalancerProps props, ServerSection section) {
        super(
                "@" + section.getProps().getServerName(),
                new InetSocketAddress("0.0.0.0", (int) Math.floor(Math.random() * (0xFFFF + 1))),
                "Section server of " + section.getName(),
                false
        );

        this.props = props;
        this.section = section;
    }

    @Override
    public Collection<ProxiedPlayer> getPlayers() {
        if (props.isShowPlayers()) {
            return section.getServers().stream()
                    .map(ServerInfo::getPlayers)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());
        } else return Collections.emptyList();
    }
}
