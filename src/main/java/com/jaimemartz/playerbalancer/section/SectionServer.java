package com.jaimemartz.playerbalancer.section;

import com.jaimemartz.playerbalancer.settings.props.features.BalancerProps;
import net.md_5.bungee.BungeeServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

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
            List<ProxiedPlayer> res = new ArrayList<>();
            section.getServers().forEach(server -> {
                res.addAll(server.getPlayers());
            });
            return res;
        } else {
            return Collections.emptyList();
        }
    }
}
