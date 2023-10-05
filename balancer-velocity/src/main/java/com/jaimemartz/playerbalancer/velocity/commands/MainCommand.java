package com.jaimemartz.playerbalancer.velocity.commands;

import com.google.common.base.Strings;
import com.jaimemartz.playerbalancer.velocity.PlayerBalancer;
import com.jaimemartz.playerbalancer.velocity.helper.PasteHelper;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextDecoration;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.AQUA;
import static net.kyori.adventure.text.format.NamedTextColor.GRAY;
import static net.kyori.adventure.text.format.NamedTextColor.GREEN;
import static net.kyori.adventure.text.format.NamedTextColor.RED;

public class MainCommand implements SimpleCommand {
    private final PlayerBalancer plugin;

    public MainCommand(PlayerBalancer plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Invocation invocation) {
        CommandSource sender = invocation.source();
        String[] args = invocation.arguments();

        if (args.length != 0) {
            switch (args[0].toLowerCase()) {
                case "paste": {
                    if (sender.hasPermission("playerbalancer.admin")) {
                        if (args.length == 2) {
                            switch (args[1].toLowerCase()) {
                                case "all": {
                                    PasteHelper.PLUGIN.send(plugin, sender);
                                    PasteHelper.VELOCITY.send(plugin, sender);
                                    PasteHelper.LOGS.send(plugin, sender);
                                    break;
                                }

                                case "plugin": {
                                    PasteHelper.PLUGIN.send(plugin, sender);
                                    break;
                                }

                                case "velocity": {
                                    PasteHelper.VELOCITY.send(plugin, sender);
                                    break;
                                }

                                case "logs": {
                                    PasteHelper.LOGS.send(plugin, sender);
                                    break;
                                }

                                default: {
                                    sender.sendMessage(text("This is not a valid argument for this command! Execute /balancer paste for help", RED));
                                }
                            }
                        } else {
                            if (sender instanceof Player) {
                                sender.sendMessage(text("Available paste types:", AQUA));

                                sender.sendMessage(text("Click one:", AQUA)
                                        .append(text(" [")
                                                .color(GRAY)
                                                .append(text("All")
                                                        .color(RED)
                                                        .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/balancer paste all"))
                                                        .hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, text("Click to paste all", RED)))
                                                )
                                                .append(Component.text("]", GRAY)))
                                        .append(text(" [")
                                                .color(GRAY)
                                                .append(text("Plugin")
                                                        .color(RED)
                                                        .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/balancer paste plugin"))
                                                        .hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, text("Click to paste plugin config", RED)))
                                                )
                                                .append(Component.text("]", GRAY))
                                        )
                                        .append(text(" [")
                                                .color(GRAY)
                                                .append(text("Velocity")
                                                        .color(RED)
                                                        .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/balancer paste velocity"))
                                                        .hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, text("Click to paste Velocity config", RED)))
                                                )
                                                .append(Component.text("]", GRAY)))
                                        .append(text(" [")
                                                .color(GRAY)
                                                .append(text("Logs")
                                                        .color(RED)
                                                        .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/balancer paste logs"))
                                                        .hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, text("Click to paste logs", RED)))
                                                )
                                                .append(Component.text("]", GRAY)))
                                );
                            } else {
                                sender.sendMessage(text("Usage: /balancer paste [all|plugin|velocity|logs]", RED));
                            }

                        }
                    } else {
                        sender.sendMessage(text("You do not have permission to execute this command!"));
                    }
                    break;
                }

                case "reload": {
                    if (sender.hasPermission("playerbalancer.admin")) {
                        sender.sendMessage(text("Reloading the configuration, this may take a while...", GREEN));
                        if (plugin.reloadPlugin()) {
                            sender.sendMessage(text("The plugin has been successfully reloaded", GREEN));
                        } else {
                            sender.sendMessage(text("Something went badly while reloading the plugin", RED));
                        }
                    } else {
                        sender.sendMessage(text("You do not have permission to execute this command!", RED));
                    }
                    break;
                }

                default: {
                    sender.sendMessage(text("This is not a valid argument for this command! Execute /balancer for help", RED));
                }
            }
        } else {
            sender.sendMessage(text(Strings.repeat("-", 53), GRAY, TextDecoration.STRIKETHROUGH));
            sender.sendMessage(text("PlayerBalancer " + plugin.getContainer().getDescription().getVersion().orElse("-.-.-"), GRAY));
            sender.sendMessage(text("Available commands:", GRAY));
            sender.sendMessage(text("/balancer", AQUA).append(text(" - ", GRAY)).append(text("Shows you this message", RED)));
            sender.sendMessage(text("/balancer paste [all|plugin|velocity|logs]", AQUA).append(text(" - ", GRAY)).append(text("Creates a paste with the important files", RED)));
            sender.sendMessage(text("/balancer reload", AQUA).append(text(" - ", GRAY)).append(text("Reloads the plugin completely", RED)));
            sender.sendMessage(text(Strings.repeat("-", 53), GRAY, TextDecoration.STRIKETHROUGH));
        }
    }
}