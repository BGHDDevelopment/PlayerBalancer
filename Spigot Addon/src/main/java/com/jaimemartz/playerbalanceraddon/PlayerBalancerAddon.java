package com.jaimemartz.playerbalanceraddon;

import org.bukkit.plugin.java.JavaPlugin;

public class PlayerBalancerAddon extends JavaPlugin {
    private PluginMessageManager manager;

    @Override
    public void onDisable() {
        manager = new PluginMessageManager(this);
        getCommand("balancer").setExecutor(new MainCommand(this));
    }

    @Override
    public void onEnable() {

    }

    public PluginMessageManager getManager() {
        return manager;
    }
}
