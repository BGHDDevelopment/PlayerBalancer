package com.jaimemartz.playerbalanceraddon;

import org.bukkit.plugin.java.JavaPlugin;

public class PlayerBalancerAddon extends JavaPlugin {
    private PluginMessageManager manager;
    private PlaceholderHandler handler;

    @Override
    public void onEnable() {
        manager = new PluginMessageManager(this);
        getCommand("spb").setExecutor(new MainCommand(this));

        if (getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            handler = new PlaceholderHandler(this);
            handler.hook();
        }
    }

    @Override
    public void onDisable() {
        //Nothing to do...
    }

    public PluginMessageManager getManager() {
        return manager;
    }
}
