package com.jaimemartz.playerbalancer.velocity.section;

import com.jaimemartz.playerbalancer.velocity.PlayerBalancer;
import com.jaimemartz.playerbalancer.velocity.commands.FallbackCommand;
import com.jaimemartz.playerbalancer.velocity.settings.props.shared.CommandProps;
import com.velocitypowered.api.proxy.Player;

public class SectionCommand extends FallbackCommand {
    private final ServerSection section;
    private final String permission;

    public SectionCommand(PlayerBalancer plugin, ServerSection section) {
        super(plugin);
        this.section = section;
        this.permission = getCommandProps().getPermission();
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return permission.isEmpty() || invocation.source().hasPermission(permission);
    }

    @Override
    public ServerSection getSection(Player player) {
        return section;
    }

    public CommandProps getCommandProps() {
        return section.getProps().getCommandProps();
    }
}
