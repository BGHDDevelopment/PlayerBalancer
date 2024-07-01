package com.bghddevelopment.partyandfriendsaddon;

import com.google.inject.Inject;
import com.jaimemartz.playerbalancer.velocity.helper.PlayerLocker;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import de.simonsator.partyandfriends.velocity.api.friends.ServerConnector;
import de.simonsator.partyandfriends.velocity.api.pafplayers.PAFPlayerClass;

import java.util.logging.Logger;

public class PBServerConnector implements ServerConnector {

    private final Logger logger;

    @Inject
    public PBServerConnector(Logger logger) {
        this.logger = logger;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        PAFPlayerClass.setServerConnector(this);
        logger.info("Enabled PBServerConnector connection for PlayerBalancer!");
    }

    @Override
    public void connect(Player player, RegisteredServer registeredServer) {
        PlayerLocker.lock(player);
        player.createConnectionRequest(registeredServer).fireAndForget();
        PlayerLocker.unlock(player);
    }

}
