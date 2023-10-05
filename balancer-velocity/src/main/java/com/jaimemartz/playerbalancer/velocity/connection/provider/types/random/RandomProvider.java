package com.jaimemartz.playerbalancer.velocity.connection.provider.types.random;

import com.jaimemartz.playerbalancer.velocity.PlayerBalancer;
import com.jaimemartz.playerbalancer.velocity.connection.provider.AbstractProvider;
import com.jaimemartz.playerbalancer.velocity.section.ServerSection;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import java.util.List;

import static com.jaimemartz.playerbalancer.velocity.utils.RandomUtils.random;

public class RandomProvider extends AbstractProvider {
    @Override
    public RegisteredServer requestTarget(PlayerBalancer plugin, ServerSection section, List<RegisteredServer> servers, Player player) {
        return random(servers);
    }
}