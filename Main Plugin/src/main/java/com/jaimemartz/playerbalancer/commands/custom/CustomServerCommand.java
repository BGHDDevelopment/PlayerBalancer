package com.jaimemartz.playerbalancer.commands.custom;

import com.jaimemartz.playerbalancer.PlayerBalancer;
import com.jaimemartz.playerbalancer.settings.props.features.CustomServerCommandProps;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.plugin.Command;

public class CustomServerCommand extends Command {
    private final PlayerBalancer plugin;
    private final CustomServerCommandProps props;

    public CustomServerCommand(PlayerBalancer plugin) {
        this(plugin, plugin.getSettings().getFeaturesProps().getCustomServerCommandProps());
    }

    private CustomServerCommand(PlayerBalancer plugin, CustomServerCommandProps props) {
        super(
                props.getCommand().getName(),
                props.getCommand().getPermission(),
                props.getCommand().getAliasesArray()
        );

        this.plugin = plugin;
        this.props = props;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        sender.sendMessage(new ComponentBuilder("Not implemented yet.").color(ChatColor.RED).create());
    }
}
