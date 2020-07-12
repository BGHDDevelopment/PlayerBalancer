package com.jaimemartz.playerbalanceraddon;

import org.bukkit.plugin.java.JavaPlugin;

public class PlayerBalancerAddon extends JavaPlugin {
    private PluginMessageManager manager;
    private PlayerBalancerPlaceholderExpansion expansion;

    @Override
    public void onEnable() {
        manager = new PluginMessageManager(this);
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
}
