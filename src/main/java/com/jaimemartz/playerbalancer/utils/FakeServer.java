package com.jaimemartz.playerbalancer.utils;

import com.jaimemartz.playerbalancer.section.ServerSection;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FakeServer implements ServerInfo {
    private final ServerSection section;

    public FakeServer(ServerSection section) {
        this.section = section;
    }

    @Override
    public String getName() {
        return "@" + section.getProps().getServerName();
    }

    @Override
    public InetSocketAddress getAddress() {
        return null;
    }

    @Override
    public Collection<ProxiedPlayer> getPlayers() {
        List<ProxiedPlayer> res = new ArrayList<>();
        section.getServers().forEach(server -> {
            res.addAll(server.getPlayers());
        });
        return res;
    }

    @Override
    public String getMotd() {
        return "Fake server of section " + section.getName();
    }

    @Override
    public boolean canAccess(CommandSender sender) {
        return true;
    }

    @Override
    public void sendData(String channel, byte[] data) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public boolean sendData(String channel, byte[] data, boolean queue) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void ping(Callback<ServerPing> callback) {
        throw new RuntimeException("Not implemented");
    }
}
