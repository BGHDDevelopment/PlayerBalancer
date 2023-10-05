package com.jaimemartz.playerbalancer.velocity.connection.provider;

import com.jaimemartz.playerbalancer.velocity.PlayerBalancer;
import com.jaimemartz.playerbalancer.velocity.section.ServerSection;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import java.util.List;

public abstract class AbstractProvider {
    public abstract RegisteredServer requestTarget(
            PlayerBalancer plugin,
            ServerSection section,
            List<RegisteredServer> servers,
            Player player
    );
}
