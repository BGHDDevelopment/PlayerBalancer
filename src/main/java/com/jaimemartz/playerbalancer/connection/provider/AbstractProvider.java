package com.jaimemartz.playerbalancer.connection.provider;

import com.jaimemartz.playerbalancer.PlayerBalancer;
import com.jaimemartz.playerbalancer.section.ServerSection;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.List;

public abstract class AbstractProvider {
    public abstract ServerInfo requestTarget(
            PlayerBalancer plugin,
            ServerSection section,
            List<ServerInfo> servers,
            ProxiedPlayer player
    );
}
