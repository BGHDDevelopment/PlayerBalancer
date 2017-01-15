package me.jaimemartz.lobbybalancer.commands;

import me.jaimemartz.faucet.Messager;
import me.jaimemartz.lobbybalancer.LobbyBalancer;
import me.jaimemartz.lobbybalancer.configuration.ConfigEntries;
import me.jaimemartz.lobbybalancer.connection.ConnectionIntent;
import me.jaimemartz.lobbybalancer.manager.PlayerLocker;
import me.jaimemartz.lobbybalancer.section.ServerSection;
import me.jaimemartz.lobbybalancer.utils.ConfigUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.config.Configuration;

import java.util.concurrent.Callable;

public class FallbackCommand extends Command {
    private final LobbyBalancer plugin;

    public FallbackCommand(LobbyBalancer plugin) {
        super(ConfigEntries.FALLBACK_COMMAND_NAME.get(), ConfigEntries.FALLBACK_COMMAND_PERMISSION.get(), (ConfigEntries.FALLBACK_COMMAND_ALIASES.get().stream()).toArray(String[]::new));
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Messager msgr = new Messager(sender);
        if (sender instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer) sender;
            ServerSection section = plugin.getSectionManager().getByServer(player.getServer().getInfo());

            Callable<ServerSection> callable = () -> {
                if (section != null) {
                    if ((ConfigEntries.FALLBACK_COMMAND_IGNORED_SECTIONS.get()).contains(section.getName())) {
                        msgr.send(ConfigEntries.UNAVAILABLE_MESSAGE.get());
                    }

                    PlayerLocker.lock(player);

                    if (ConfigEntries.FALLBACK_COMMAND_ARGUMENTS.get() && args.length == 1) {
                        ServerSection target = plugin.getSectionManager().getByName(args[0]);

                        if (target == null) {
                            msgr.send(ConfigEntries.UNKNOWN_SECTION_MESSAGE.get());
                        }

                        return target;
                    } else {
                        Configuration rules = plugin.getConfig().getSection("settings.backward-command.rules");

                        if (ConfigUtils.isSet(rules, section.getName())) {
                            String bind = rules.getString(section.getName());
                            plugin.getSectionManager().getByName(bind);
                        } else {
                            return section.getParent();
                        }
                    }
                } else {
                    if (ConfigEntries.FALLBACK_PRINCIPAL_ENABLED.get()) {
                        return plugin.getSectionManager().getPrincipal();
                    }
                }

                return null;
            };

            try {
                ServerSection target = callable.call();
                if (target != null) {
                    new ConnectionIntent(plugin, player, target) {
                        @Override
                        public void connect(ServerInfo server) {
                            player.connect(server);
                            PlayerLocker.unlock(player);
                        }
                    };
                } else {
                    msgr.send(ConfigEntries.UNAVAILABLE_MESSAGE.get());
                }
            } catch (Exception e) {
                msgr.send(ConfigEntries.UNAVAILABLE_MESSAGE.get());
            }
        } else {
            msgr.send(ChatColor.RED + "This command can only be executed by a player");
        }
    }
}
