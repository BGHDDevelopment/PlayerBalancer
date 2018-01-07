package com.jaimemartz.playerbalancer.commands;

import com.jaimemartz.playerbalancer.PlayerBalancer;
import com.jaimemartz.playerbalancer.settings.props.features.CustomFindCommandProps;
import com.jaimemartz.playerbalancer.utils.MessageUtils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
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
        if (args.length == 1) {
            ProxiedPlayer player = plugin.getProxy().getPlayer(args[0]);

            if (player != null && player.getServer() != null) {
                MessageUtils.send(player, props.getFormats().getResult(), (str) ->
                        str.replace("{server}", player.getServer().getInfo().getName())
                                .replace("{name}", player.getName())
                );
            } else {
                MessageUtils.send(player, props.getFormats().getMissing(), (str) ->
                        str.replace("{name}", player.getName())
                );
            }
        } else {
            MessageUtils.send(sender, props.getFormats().getUsage());
        }
    }
}
