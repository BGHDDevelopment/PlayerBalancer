package me.jaimemartz.lobbybalancer;

import lombok.Getter;
import me.jaimemartz.faucet.ConfigFactory;
import me.jaimemartz.lobbybalancer.commands.FallbackCommand;
import me.jaimemartz.lobbybalancer.commands.MainCommand;
import me.jaimemartz.lobbybalancer.commands.ManageCommand;
import me.jaimemartz.lobbybalancer.configuration.ConfigEntries;
import me.jaimemartz.lobbybalancer.connection.ServerAssignRegistry;
import me.jaimemartz.lobbybalancer.listener.*;
import me.jaimemartz.lobbybalancer.manager.PlayerLocker;
import me.jaimemartz.lobbybalancer.ping.StatusManager;
import me.jaimemartz.lobbybalancer.section.SectionManager;
import me.jaimemartz.lobbybalancer.utils.DigitUtils;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import org.bstats.Metrics;
import org.inventivetalent.update.bungee.BungeeUpdater;

import java.io.IOException;
import java.util.logging.Level;

public class LobbyBalancer extends Plugin {
    private static final int LAST_VER_CONFIG_UPDATE = 20950;

    @Getter private ConfigFactory factory;
    @Getter private boolean failed = false;
    @Getter private StatusManager statusManager;
    @Getter private SectionManager sectionManager;
    @Getter private static LobbyBalancer instance;

    private Command fallbackCommand, mainCommand, manageCommand;
    private Listener connectListener, kickListener, messageListener, reloadListener;

    @Override
    public void onEnable() {
        instance = this;

        if (factory == null) {
            factory = new ConfigFactory(this);
            factory.register(0, "config.yml");
            factory.submit(ConfigEntries.class);
        }

        this.enable();

        //Metrics (https://bstats.org/)
        Metrics metrics = new Metrics(this);
        metrics.addCustomChart(new Metrics.SingleLineChart("configured_sections", () -> sectionManager.getSections().size()));
    }

    private void enable() {
        factory.load(0, true);

        mainCommand = new MainCommand(this);
        getProxy().getPluginManager().registerCommand(this, mainCommand);

        String text = ConfigEntries.CONFIG_VERSION.get();
        int configVersion = DigitUtils.getDigits(text, 5);
        if (configVersion < LAST_VER_CONFIG_UPDATE) {
            failed = true;
            throw new IllegalStateException("Your config is outdated, please reset it and configure it again");
        }

        if (ConfigEntries.PLUGIN_ENABLED.get()) {
            if (ConfigEntries.SILENT_STARTUP.get()) {
                getLogger().setLevel(Level.WARNING);
            }

            if (ConfigEntries.AUTO_RELOAD_ENABLED.get()) {
                reloadListener = new ProxyReloadListener(this);
                getProxy().getPluginManager().registerListener(this, reloadListener);
            }

            try {
                new BungeeUpdater(this, 10788);
            } catch (IOException e) {
                e.printStackTrace();
            }

            sectionManager = new SectionManager(this);

            try {
                sectionManager.load();

                statusManager = new StatusManager();
                if (ConfigEntries.SERVER_CHECK_ENABLED.get()) {
                    statusManager.start(this);
                }

                if (ConfigEntries.FALLBACK_COMMAND_ENABLED.get()) {
                    fallbackCommand = new FallbackCommand(this);
                    getProxy().getPluginManager().registerCommand(this, fallbackCommand);
                }

                connectListener = new ServerConnectListener(this);
                getProxy().getPluginManager().registerListener(this, connectListener);

                messageListener = new PluginMessageListener(this);
                getProxy().getPluginManager().registerListener(this, messageListener);

                manageCommand = new ManageCommand(this);
                getProxy().getPluginManager().registerCommand(this, manageCommand);

                getProxy().getPluginManager().registerListener(this, new PlayerDisconnectListener(this));

                getProxy().registerChannel("LobbyBalancer");

                if (ConfigEntries.RECONNECT_KICK_ENABLED.get()) {
                    kickListener = new ServerKickListener(this);
                    getProxy().getPluginManager().registerListener(this, kickListener);
                }

                getLogger().info("The plugin has finished loading without any problems");
            } catch (RuntimeException e) {
                failed = true;
                getLogger().severe("The plugin could not continue loading due to an unexpected exception");
                e.printStackTrace();
            }
        } else {
            getLogger().warning("-----------------------------------------------------");
            getLogger().warning("WARNING: This plugin is disabled, do not forget to set enabled on the config to true");
            getLogger().warning("Nothing is going to work until you do that, you can reload me by using the /balancer command");
            getLogger().warning("-----------------------------------------------------");
        }
    }

    @Override
    public void onDisable() {
        disable();
    }

    private void disable() {
        getProxy().getPluginManager().unregisterCommand(mainCommand);
        mainCommand = null;

        if (ConfigEntries.PLUGIN_ENABLED.get()) {
            //Do not try to do anything if the plugin has not loaded correctly
            if (isFailed()) return;

            if (ConfigEntries.AUTO_RELOAD_ENABLED.get()) {
                getProxy().getPluginManager().unregisterListener(reloadListener);
                reloadListener = null;
            }

            if (ConfigEntries.SERVER_CHECK_ENABLED.get()) {
                statusManager.stop();
            }

            if (ConfigEntries.FALLBACK_COMMAND_ENABLED.get()) {
                getProxy().getPluginManager().unregisterCommand(fallbackCommand);
                fallbackCommand = null;
            }

            getProxy().getPluginManager().unregisterListener(connectListener);
            connectListener = null;

            getProxy().getPluginManager().unregisterListener(messageListener);
            messageListener = null;

            getProxy().getPluginManager().unregisterCommand(manageCommand);
            manageCommand = null;

            if (ConfigEntries.RECONNECT_KICK_ENABLED.get()) {
                getProxy().getPluginManager().unregisterListener(kickListener);
                kickListener = null;
            }

            sectionManager.flush();

            if (ConfigEntries.ASSIGN_TARGETS_ENABLED.get()) {
                ServerAssignRegistry.getTable().clear();
            }
        }
        PlayerLocker.flush();
        failed = false;
    }

    public void reloadPlugin() {
        getLogger().info("Reloading the plugin...");
        long starting = System.currentTimeMillis();

        this.disable();
        this.enable();

        long ending = System.currentTimeMillis() - starting;
        getLogger().info(String.format("The plugin has been reloaded, took %sms", ending));
    }

    public Configuration getConfigHandle() {
        return factory.get(0).getHandle();
    }
}
