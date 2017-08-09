package com.jaimemartz.playerbalancer.listener;

import com.jaimemartz.playerbalancer.PlayerBalancer;
import com.jaimemartz.playerbalancer.connection.ConnectionIntent;
import com.jaimemartz.playerbalancer.manager.PlayerLocker;
import com.jaimemartz.playerbalancer.section.ServerSection;
import com.jaimemartz.playerbalancer.settings.ConfigEntries;
import com.jaimemartz.playerbalancer.utils.MessageUtils;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class ServerKickListener implements Listener {
    private final PlayerBalancer plugin;

    public ServerKickListener(PlayerBalancer plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onKick(ServerKickEvent event) {
        ProxiedPlayer player = event.getPlayer();
        ServerInfo from = event.getKickedFrom();

        //Section the player is going to be reconnected
        Callable<ServerSection> callable = () -> {
            if (player.getServer() == null) {
                if (ConfigEntries.RECONNECT_KICK_FORCE_PRINCIPAL.get()) {
                    return plugin.getSectionManager().getPrincipal();
                } else {
                    return null;
                }
            }

            if (!player.getServer().getInfo().equals(from)) {
                return null;
            }

            ServerSection section = plugin.getSectionManager().getByServer(from);

            if (section != null) {
                if ((ConfigEntries.RECONNECT_KICK_IGNORED_SECTIONS.get()).contains(section.getName())) {
                    return null;
                }
            }

            AtomicBoolean matches = new AtomicBoolean(false);
            String reason = TextComponent.toPlainText(event.getKickReasonComponent());
            for (String string : ConfigEntries.RECONNECT_KICK_REASONS.get()) {
                if (reason.matches(string) || reason.contains(string)) {
                    matches.set(true);
                    break;
                }
            }

            if (ConfigEntries.RECONNECT_KICK_INVERTED.get()) {
                matches.set(!matches.get());
            }

            if (ConfigEntries.RECONNECT_KICK_PRINT_INFO.get()) {
                plugin.getLogger().info(String.format("Kick Reason: \"%s\", Found Match: %s", TextComponent.toPlainText(event.getKickReasonComponent()), matches));
            }

            if (matches.get()) {
                if (section != null) {
                    Configuration rules = plugin.getConfigHandle().getSection("settings.reconnect-kick.rules");
                    String name = rules.getString(section.getName());
                    ServerSection target = plugin.getSectionManager().getByName(name);

                    if (target == null) {
                        target = section.getParent();
                    }

                    if (ConfigEntries.RECONNECT_KICK_RESTRICTED.get()) {
                        if (section.getPosition() >= 0 && target.getPosition() < 0) {
                            return null;
                        }
                    }

                    return target;
                } else {
                    if (ConfigEntries.FALLBACK_PRINCIPAL_ENABLED.get()) {
                        return plugin.getSectionManager().getPrincipal();
                    }
                }
            }
            return null;
        };

        try {
            ServerSection section = callable.call();
            if (section != null) {
                List<ServerInfo> servers = new ArrayList<>();
                servers.addAll(section.getServers());

                if (ConfigEntries.RECONNECT_KICK_EXCLUDE_FROM.get()) {
                    servers.remove(from);
                }

                new ConnectionIntent(plugin, player, section, servers) {
                    @Override
                    public void connect(ServerInfo server, Callback<Boolean> callback) {
                        PlayerLocker.lock(player);
                        MessageUtils.send(player, ConfigEntries.KICK_MESSAGE.get(),
                                (str) -> str.replace("{from}", from.getName())
                                        .replace("{to}", server.getName())
                                        .replace("{reason}", event.getKickReason()));
                        event.setCancelled(true);
                        event.setCancelServer(server);
                        plugin.getProxy().getScheduler().schedule(plugin, () -> {
                            PlayerLocker.unlock(player);
                        }, 5, TimeUnit.SECONDS);
                        callback.done(true, null);
                    }
                };
            }
        } catch (Exception e) {
            //Nothing to do
        }
    }
}