package com.jaimemartz.playerbalancer.velocity.connection.provider.types;

import com.jaimemartz.playerbalancer.velocity.PlayerBalancer;
import com.jaimemartz.playerbalancer.velocity.connection.provider.AbstractProvider;
import com.jaimemartz.playerbalancer.velocity.section.ServerSection;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import java.util.List;

public class NullProvider extends AbstractProvider {
    @Override
    public RegisteredServer requestTarget(PlayerBalancer plugin, ServerSection section, List<RegisteredServer> servers, Player player) {
        return null;
    }
}
