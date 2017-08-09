package com.jaimemartz.playerbalancer;

import ch.jalu.injector.Injector;
import ch.jalu.injector.InjectorBuilder;
import com.jaimemartz.playerbalancer.commands.FallbackCommand;
import com.jaimemartz.playerbalancer.commands.MainCommand;
import com.jaimemartz.playerbalancer.commands.ManageCommand;
import com.jaimemartz.playerbalancer.connection.ServerAssignRegistry;
import com.jaimemartz.playerbalancer.listener.*;
import com.jaimemartz.playerbalancer.manager.PasteHelper;
import com.jaimemartz.playerbalancer.manager.PlayerLocker;
import com.jaimemartz.playerbalancer.ping.StatusManager;
import com.jaimemartz.playerbalancer.section.SectionManager;
import com.jaimemartz.playerbalancer.settings.Settings;
import com.jaimemartz.playerbalancer.settings.SettingsProvider;
import com.jaimemartz.playerbalancer.settings.beans.SectionHandler;
import com.jaimemartz.playerbalancer.settings.types.*;
import lombok.Getter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import org.bstats.bungeecord.Metrics;
import org.bstats.bungeecord.Metrics.SingleLineChart;
import org.inventivetalent.update.bungee.BungeeUpdater;

import java.io.IOException;
import java.util.logging.Level;
import java.util.stream.Stream;

public class PlayerBalancer extends Plugin {
    @Getter private boolean failed = false;

    //Private instances
    private Injector injector;
    private Settings settings;
    private StatusManager statusManager;
    private SectionManager sectionManager;

    private Command fallbackCommand, mainCommand, manageCommand;
    private Listener connectListener, kickListener, messageListener, reloadListener;

    @Override
    public void onEnable() {
        getDataFolder().mkdir();

        injector = new InjectorBuilder()
                .addDefaultHandlers(getClass().getPackage().getName())
                .create();

        injector.register(PlayerBalancer.class, this);
        injector.register(ProxyServer.class, this.getProxy());
        injector.register(SettingsProvider.class, new SettingsProvider(this.getDataFolder()));
        injector.registerProvider(Settings.class, SettingsProvider.class);

        settings = injector.getSingleton(Settings.class);
        injector.register(SectionHandler.class, settings.getProperty(SectionsHolder.SECTION_HOLDER));

        Metrics metrics = new Metrics(this);
        if (this.enable()) {
            metrics.addCustomChart(new SingleLineChart("configured_sections", () -> sectionManager.getSections().size()));
        }
    }

    private boolean enable() {
        mainCommand = new MainCommand(this);
        getProxy().getPluginManager().registerCommand(this, mainCommand);

        if (settings.getProperty(GeneralProperties.ENABLED)) {
            if (settings.getProperty(GeneralProperties.SILENT)) {
                getLogger().setLevel(Level.WARNING);
            }

            if (settings.getProperty(GeneralProperties.AUTO_RELOAD)) {
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
                if (settings.getProperty(CheckerProperties.ENABLED)) {
                    statusManager.start(this);
                }

                if (settings.getProperty(CommandProperties.ENABLED)) {
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

                getProxy().registerChannel("PlayerBalancer");

                Stream.of(PasteHelper.values()).forEach(a -> a.setUrl(null));

                if (settings.getProperty(ReconnectorProperties.ENABLED)) {
                    kickListener = new ServerKickListener(this);
                    getProxy().getPluginManager().registerListener(this, kickListener);
                }

                getLogger().info("The plugin has finished loading without any problems");
                return true;
            } catch (RuntimeException e) {
                this.failed = true;
                getLogger().severe("The plugin could not continue loading due to an unexpected exception");
                e.printStackTrace();
            }
        } else {
            getLogger().warning("-----------------------------------------------------");
            getLogger().warning("WARNING: This plugin is disabled, do not forget to set enabled on the config to true");
            getLogger().warning("Nothing is going to work until you do that, you can reload me by using the /balancer command");
            getLogger().warning("-----------------------------------------------------");
        }
        return false;
    }

    @Override
    public void onDisable() {
        disable();
    }

    private void disable() {
        getProxy().getPluginManager().unregisterCommand(mainCommand);
        mainCommand = null;

        if (settings.getProperty(GeneralProperties.ENABLED)) {
            //Do not try to do anything if the plugin has not loaded correctly
            if (isFailed()) return;

            if (settings.getProperty(GeneralProperties.AUTO_RELOAD)) {
                getProxy().getPluginManager().unregisterListener(reloadListener);
                reloadListener = null;
            }

            if (settings.getProperty(CheckerProperties.ENABLED)) {
                statusManager.stop();
            }

            if (settings.getProperty(CommandProperties.ENABLED)) {
                getProxy().getPluginManager().unregisterCommand(fallbackCommand);
                fallbackCommand = null;
            }

            getProxy().getPluginManager().unregisterListener(connectListener);
            connectListener = null;

            getProxy().getPluginManager().unregisterListener(messageListener);
            messageListener = null;

            getProxy().getPluginManager().unregisterCommand(manageCommand);
            manageCommand = null;

            if (settings.getProperty(ReconnectorProperties.ENABLED)) {
                getProxy().getPluginManager().unregisterListener(kickListener);
                kickListener = null;
            }

            sectionManager.flush();

            if (settings.getProperty(GeneralProperties.ASSIGN_TARGETS)) {
                ServerAssignRegistry.getTable().clear();
            }
        }

        PlayerLocker.flush();
        failed = false;
    }

    public boolean reloadPlugin() {
        getLogger().info("Reloading the plugin...");
        long starting = System.currentTimeMillis();

        this.disable();
        settings.reload();
        this.enable();

        long ending = System.currentTimeMillis() - starting;
        getLogger().info(String.format("The plugin has been reloaded, took %sms", ending));

        return !failed;
    }
}
