package me.jaimemartz.lobbybalancer.listener;

import me.jaimemartz.lobbybalancer.LobbyBalancer;
import me.jaimemartz.lobbybalancer.configuration.ConfigEntries;
import me.jaimemartz.lobbybalancer.connection.ConnectionIntent;
import me.jaimemartz.lobbybalancer.section.ServerSection;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.util.concurrent.atomic.AtomicBoolean;

public class ServerKickListener implements Listener {
    private final LobbyBalancer plugin;

    public ServerKickListener(LobbyBalancer plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onKick(ServerKickEvent event) {
        ProxiedPlayer player = event.getPlayer();
        ServerInfo from = event.getKickedFrom();

        if (player.getServer() == null) {
            return;
        }

        if (!player.getServer().getInfo().equals(from)) {
            return;
        }

        ServerSection section = plugin.getSectionManager().getByServer(from);
        if (section != null) {
            if ((ConfigEntries.RECONNECT_KICK_IGNORED_SECTIONS.get()).contains(section.getName())) {
                return;
            }

            Configuration rules = plugin.getConfig().getSection("settings.reconnect-kick.rules");
            String name = rules.getString(section.getName());
            ServerSection target = plugin.getSectionManager().getByName(name);

            if (target == null) {
                target = section.getParent();
                if (target == null) {
                    return;
                }
            }

            AtomicBoolean matches = new AtomicBoolean(false);
            String reason = TextComponent.toPlainText(event.getKickReasonComponent());
            for (String pattern : ConfigEntries.RECONNECT_KICK_REASONS.get()) {
                if (reason.matches(pattern)) {
                    matches.set(true);
                    break;
                }
            }

            if (ConfigEntries.RECONNECT_KICK_INVERTED.get()) {
                matches.set(!matches.get());
            }

            if (matches.get()) {
                new ConnectionIntent(plugin, player, target) {
                    @Override
                    public void connect(ServerInfo server) {
                        LobbyBalancer.checkSendMessage(player, ConfigEntries.RECONNECT_KICK_MESSAGE.get()
                                .replace("{from}", from.getName())
                                .replace("{to}", server.getName())
                                .replace("{reason}", reason)
                        );

                        event.setCancelled(true);
                        event.setCancelServer(server);
                    }
                };
            }

            if (ConfigEntries.RECONNECT_KICK_PRINT_INFO.get()) {
                LobbyBalancer.printStartupInfo(String.format("Kick Reason: \"%s\", Found Match: %s", TextComponent.toPlainText(event.getKickReasonComponent()), matches.get()));
            }
        }
    }
}
