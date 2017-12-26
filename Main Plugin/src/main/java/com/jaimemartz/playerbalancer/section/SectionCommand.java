package com.jaimemartz.playerbalancer.section;

import com.jaimemartz.playerbalancer.PlayerBalancer;
import com.jaimemartz.playerbalancer.commands.AbstractMoveCommand;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class SectionCommand extends AbstractMoveCommand {
    private final ServerSection section;

    public SectionCommand(PlayerBalancer plugin, ServerSection section) {
        super(plugin, section.getProps().getCommandProps());
        this.section = section;
    }

    @Override
    public ServerSection getSection(ProxiedPlayer player) {
        return section;
    }
}
