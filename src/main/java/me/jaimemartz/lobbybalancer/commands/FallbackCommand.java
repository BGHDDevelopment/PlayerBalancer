package me.jaimemartz.lobbybalancer.commands;

import me.jaimemartz.faucet.Messager;
import me.jaimemartz.lobbybalancer.LobbyBalancer;
import me.jaimemartz.lobbybalancer.configuration.ConfigEntries;
import me.jaimemartz.lobbybalancer.connection.ConnectionIntent;
import me.jaimemartz.lobbybalancer.section.ServerSection;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
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

            Callable<ServerSection> callable = () -> {
                ServerSection section = plugin.getSectionManager().getByServer(player.getServer().getInfo());

                if (section != null) {
                    /* TODO REFERENCE TO ServerKickListener
                    if (ConfigEntries.FALLBACK_COMMAND_RESTRICTED.get() && section.isPrincipal()) {
                        msgr.send(ConfigEntries.UNAVAILABLE_MESSAGE.get());
                        return null;
                    }
                    */

                    if ((ConfigEntries.FALLBACK_COMMAND_IGNORED_SECTIONS.get()).contains(section.getName())) {
                        msgr.send(ConfigEntries.UNAVAILABLE_MESSAGE.get());
                        return null;
                    }

                    if (ConfigEntries.FALLBACK_COMMAND_ARGUMENTS.get() && args.length == 1) {
                        ServerSection target = plugin.getSectionManager().getByName(args[0]);

                        if (target == null) {
                            msgr.send(ConfigEntries.UNKNOWN_SECTION_MESSAGE.get());
                        }

                        return target;
                    } else {
                        Configuration rules = plugin.getConfig().getSection("settings.fallback-command.rules");
                        String bind = rules.getString(section.getName());
                        ServerSection target = plugin.getSectionManager().getByName(bind);

                        return target == null ? section.getParent() : target;
                    }
                } else {
                    if (ConfigEntries.FALLBACK_PRINCIPAL_ENABLED.get()) {
                        return plugin.getSectionManager().getPrincipal();
                    }
                }

                return null;
            };

            try {
                ServerSection section = callable.call();
                if (section != null) {
                    ConnectionIntent.connect(plugin, player, section);
                }
            } catch (Exception e) {
                //Nothing to do
            }
        } else {
            msgr.send(ChatColor.RED + "This command can only be executed by a player");
        }
    }
}
