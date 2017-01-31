package me.jaimemartz.lobbybalancer.commands;

import me.jaimemartz.faucet.Messager;
import me.jaimemartz.lobbybalancer.LobbyBalancer;
import me.jaimemartz.lobbybalancer.configuration.ConfigEntries;
import me.jaimemartz.lobbybalancer.connection.ConnectionIntent;
import me.jaimemartz.lobbybalancer.section.ServerSection;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;


public class ManageCommand extends Command {
    private final LobbyBalancer plugin;

    public ManageCommand(LobbyBalancer plugin) {
        super("section");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Messager msgr = new Messager(sender);
        if (sender.hasPermission("lobbybalancer.admin")) {
            if (args.length != 0) {
                switch (args[0].toLowerCase()) {
                    case "connect": {
                        if (args.length >= 2) {
                            String input = args[1];
                            ServerSection section = plugin.getSectionManager().getByName(input);
                            if (section != null) {
                                if (args.length == 3) {
                                    ProxiedPlayer player = plugin.getProxy().getPlayer(args[2]);
                                    if (player != null) {
                                        ConnectionIntent.simple(plugin, player, section);
                                    } else {
                                        msgr.send(ChatColor.RED + "There is no player with that name connected to this proxy");
                                    }
                                } else {
                                    if (sender instanceof ProxiedPlayer) {
                                        ConnectionIntent.simple(plugin, (ProxiedPlayer) sender, section);
                                    } else {
                                        msgr.send(ChatColor.RED + "This command can only be executed by a player");
                                    }
                                }
                            } else {
                                msgr.send(ConfigEntries.UNKNOWN_SECTION_MESSAGE.get());
                            }
                        } else {
                            sendHelper(msgr);
                        }
                        break;
                    }

                    case "info": {
                        if (args.length == 2) {
                            String input = args[1];
                            ServerSection section = plugin.getSectionManager().getByName(input);
                            if (section != null) {
                                //Information about the section %s
                            } else {
                                msgr.send(ConfigEntries.UNKNOWN_SECTION_MESSAGE.get());
                            }
                        } else {
                            sendHelper(msgr);
                        }
                        break;
                    }

                    case "list": {
                        TextComponent message = new TextComponent("These are the configured sections");
                        message.setColor(ChatColor.GREEN);

                        //GRIS Y VERDE CLARO
                        //Format: Section %s (Info)
                        plugin.getSectionManager().getSections().forEach((name, section) -> {
                            ComponentBuilder builder = new ComponentBuilder("Section %s (Info)");
                            TextComponent extra = new TextComponent(builder.create());

                            message.addExtra("\n");
                            message.addExtra(extra);
                        });

                        //TODO X players are connected to your network

                        break;
                    }

                    default: {
                        msgr.send(ChatColor.RED + "This is not a valid argument for this command!");
                        sendHelper(msgr);
                    }
                }
            } else {
                sendHelper(msgr);
            }
        } else {
            msgr.send(ChatColor.RED + "You do not have permission to execute this command!");
        }
    }

    private void sendHelper(Messager msgr) {
        msgr.send(
                "&e=====================================================",
                "&7Available commands:",
                "&3/section list &7- &cTells you which sections are configured in the plugin",
                "&3/section info <section> &7- &cTells you info about the section",
                "&3/section connect [section] <player> &7- &cConnects you or the specified player to that section",
                "&e====================================================="
        );
    }
}
