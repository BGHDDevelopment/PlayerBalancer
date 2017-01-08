package me.jaimemartz.lobbybalancer.section;

import me.jaimemartz.faucet.Messager;
import me.jaimemartz.lobbybalancer.LobbyBalancer;
import me.jaimemartz.lobbybalancer.connection.ConnectionIntent;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.List;

public class SectionCommand extends Command {
    private final LobbyBalancer plugin;
    private final ServerSection section;

    public SectionCommand(LobbyBalancer plugin, String name, String permission, List<String> aliases, ServerSection section) {
        super(name, permission, aliases.stream().toArray(String[]::new));
        this.plugin = plugin;
        this.section = section;
        plugin.getProxy().getPluginManager().registerCommand(plugin, this);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Messager msgr = new Messager(sender);
        if (sender instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer) sender;
            new ConnectionIntent(plugin, player, section) {
                @Override
                public void connect(ServerInfo server) {
                    player.connect(server);
                }
            };
        } else {
            msgr.send(ChatColor.RED + "This command can only be executed by a player");
        }
    }
}
