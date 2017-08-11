package com.jaimemartz.playerbalancer.listener;

import com.jaimemartz.playerbalancer.PlayerBalancer;
import com.jaimemartz.playerbalancer.settings.Settings;
import com.jaimemartz.playerbalancer.settings.types.SectionsHolder;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import javax.inject.Inject;

public class ServerKickListener implements Listener {
    @Inject
    private Settings settings;

    @Inject
    private SectionsHolder holder;

    @Inject
    private PlayerBalancer plugin;

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onKick(ServerKickEvent event) {
        /*
        ProxiedPlayer player = event.getPlayer();
        ServerInfo from = event.getKickedFrom();

        //Section the player is going to be reconnected
        Callable<ServerSection> callable = () -> {
            if (player.getServer() == null) {
                if (settings.getProperty(ReconnectorProperties.FORCE_PRINCIPAL)) {
                    return holder.getPrincipal();
                } else {
                    return null;
                }
            }

            if (!player.getServer().getInfo().equals(from)) {
                return null;
            }

            ServerSection current = holder.getByServer(from);

            if (current != null) {
                if (settings.getProperty(ReconnectorProperties.IGNORED_SECTIONS).contains(current.getName())) {
                    return null;
                }
            }

            AtomicBoolean matches = new AtomicBoolean(false);
            String reason = TextComponent.toPlainText(event.getKickReasonComponent());

            for (String string : settings.getProperty(ReconnectorProperties.REASONS)) {
                if (reason.matches(string) || reason.contains(string)) {
                    matches.set(true);
                    break;
                }
            }

            if (settings.getProperty(ReconnectorProperties.INVERTED)) {
                matches.set(!matches.get());
            }

            if (settings.getProperty(ReconnectorProperties.DEBUG)) {
                plugin.getLogger().info(String.format("Kick Reason: \"%s\", Found Match: %s", TextComponent.toPlainText(event.getKickReasonComponent()), matches));
            }

            if (matches.get()) {
                if (current != null) {
                    MapBean rules = settings.getProperty(CommandProperties.RULES);
                    String bind = rules.getMap().get(current.getName());
                    ServerSection target = holder.getByName(bind);

                    if (target == null) {
                        target = current.getParent();
                    }

                    if (settings.getProperty(ReconnectorProperties.RESTRICTED)) {
                        if (current.getPosition() >= 0 && target.getPosition() < 0) {
                            return null;
                        }
                    }

                    return target;
                } else {
                    if (settings.getProperty(GeneralProperties.FALLBACK_PRINCIPAL)) {
                        return holder.getPrincipal();
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
        */
    }
}