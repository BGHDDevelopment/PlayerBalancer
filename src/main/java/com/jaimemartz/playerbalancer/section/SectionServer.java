package com.jaimemartz.playerbalancer.section;

import net.md_5.bungee.BungeeServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SectionServer extends BungeeServerInfo {
    private final ServerSection section;

    public SectionServer(ServerSection section) {

        super(
                "@" + section.getProps().getServerName(),
                new InetSocketAddress("0.0.0.0", (int) Math.floor(Math.random() * (0xFFFF + 1))),
                "Section server of " + section.getName(),
                false
        );

        this.section = section;
    }

    @Override
    public Collection<ProxiedPlayer> getPlayers() {
        List<ProxiedPlayer> res = new ArrayList<>();
        section.getServers().forEach(server -> {
            res.addAll(server.getPlayers());
        });
        return res;
    }
}
