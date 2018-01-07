package com.jaimemartz.playerbalancer.commands;

import com.jaimemartz.playerbalancer.PlayerBalancer;
import com.jaimemartz.playerbalancer.settings.props.features.CustomFindCommandProps;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.plugin.Command;

public class CustomFindCommand extends Command {
    private final PlayerBalancer plugin;
    private final CustomFindCommandProps props;

    public CustomFindCommand(PlayerBalancer plugin) {
        this(plugin, plugin.getSettings().getFeaturesProps().getCustomFindCommandProps());
    }

    private CustomFindCommand(PlayerBalancer plugin, CustomFindCommandProps props) {
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
