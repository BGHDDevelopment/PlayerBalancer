package com.jaimemartz.playerbalancer.section;

import com.jaimemartz.playerbalancer.PlayerBalancer;
import com.jaimemartz.playerbalancer.commands.FallbackCommand;
import com.jaimemartz.playerbalancer.settings.props.shared.CommandProps;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class SectionCommand extends FallbackCommand {
    private final ServerSection section;

    public SectionCommand(PlayerBalancer plugin, CommandProps props, ServerSection section) {
        super(plugin, props);
        this.section = section;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        super.execute(sender, args);
    }

    @Override
    public ServerSection getSection(ProxiedPlayer player) {
        return section;
    }
}
