package com.bghddevelopment.partyandfriendsaddon;
import com.jaimemartz.playerbalancer.helper.PlayerLocker;
import de.simonsator.partyandfriends.api.PAFExtension;
import de.simonsator.partyandfriends.api.friends.ServerConnector;
import de.simonsator.partyandfriends.api.pafplayers.PAFPlayerClass;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class PBServerConnector extends PAFExtension implements ServerConnector {

    /*
    This was updated to use the updated PartyAndFriends API and newer PlayerBalancer versions.
    */

    public void onEnable() {
        PAFPlayerClass.setServerConnector(this);
        ProxyServer.getInstance().getConsole().sendMessage(TextComponent.fromLegacyText("Enabled PBServerConnector connection for PlayerBalancer!"));
        registerAsExtension();
    }

    public void connect(final ProxiedPlayer pPlayer, final ServerInfo pServerInfo) {
        PlayerLocker.lock(pPlayer);
        pPlayer.connect(pServerInfo);
        PlayerLocker.unlock(pPlayer);
    }
}
