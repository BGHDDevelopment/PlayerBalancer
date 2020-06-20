package com.jaimemartz.playerbalancer.section;

import com.jaimemartz.playerbalancer.settings.props.features.BalancerProps;
import net.md_5.bungee.BungeeServerInfo;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

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

    @Override
    public void sendData(String channel, byte[] data) {
        this.sendData(channel, data, true);
    }

    @Override
    public boolean sendData(String channel, byte[] data, boolean queue) {
        // Nothing to do
        return true;
    }

    @Override
    public void ping(Callback<ServerPing> callback) {
        this.ping(callback, ProxyServer.getInstance().getProtocolVersion());
    }

    @Override
    public void ping(Callback<ServerPing> callback, int protocolVersion) {
        ServerPing ping = new ServerPing();

        ping.setDescriptionComponent(new TextComponent(
                TextComponent.fromLegacyText(this.getMotd())
        ));

        ping.setVersion(new ServerPing.Protocol(
                ProxyServer.getInstance().getName(),
                protocolVersion
        ));

        Collection<ProxiedPlayer> players = getPlayers();
        ping.setPlayers(new ServerPing.Players(
                Integer.MAX_VALUE,
                players.size(),
                players.stream().map(
                        player -> new ServerPing.PlayerInfo(player.getName(), player.getUniqueId())
                ).toArray(ServerPing.PlayerInfo[]::new)
        ));

        callback.done(ping, null);
    }
}
