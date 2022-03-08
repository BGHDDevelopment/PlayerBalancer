package com.jaimemartz.playerbalanceraddon;

import org.bukkit.plugin.java.JavaPlugin;

public class PlayerBalancerAddon extends JavaPlugin {
    private PluginMessageManager manager;
    private PlayerBalancerPlaceholderExpansion expansion;
    private PlayerBalancerAddon plugin;

    @Override
    public void onEnable() {
        manager = new PluginMessageManager(this);
        plugin = this;
        getCommand("spb").setExecutor(new MainCommand(this));

        if (getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            expansion = new PlayerBalancerPlaceholderExpansion(this);
            expansion.register();
        }
    }

    @Override
    public void onDisable() {
        // Nothing to do...
    }

    public PluginMessageManager getManager() {
        return manager;
    }

    // This just allows people to do this programatically.
    // I'm doing this in github.dev, pls no murder for syntax issues.
    public void sendPlayerToServerAPI(Player player, String serverName){
        Player target = plugin.getServer().getPlayer(player.getName());
        if(target == null){
            Bukkit.getServer().getConsoleSender().sendMessage("Couldn't send " + target.getName() + " to the specified Server.");
            return;
        }
        target.sendMessage("Attempting to send you to server: " + serverName + "!");
        this.getManager().sendplayer(target, serverName);
        return;
    }
}
