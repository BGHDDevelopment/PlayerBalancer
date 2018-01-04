package com.jaimemartz.playerbalancer.section;

import com.jaimemartz.playerbalancer.PlayerBalancer;
import com.jaimemartz.playerbalancer.commands.FallbackCommand;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class SectionCommand extends FallbackCommand {
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
