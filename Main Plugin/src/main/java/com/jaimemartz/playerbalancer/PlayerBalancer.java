package com.jaimemartz.playerbalancer;

import com.google.common.reflect.TypeToken;
import com.jaimemartz.playerbalancer.commands.MainCommand;
import com.jaimemartz.playerbalancer.commands.ManageCommand;
import com.jaimemartz.playerbalancer.connection.ServerAssignRegistry;
import com.jaimemartz.playerbalancer.listeners.PlayerDisconnectListener;
import com.jaimemartz.playerbalancer.listeners.ProxyReloadListener;
import com.jaimemartz.playerbalancer.listeners.ServerConnectListener;
import com.jaimemartz.playerbalancer.listeners.ServerKickListener;
import com.jaimemartz.playerbalancer.manager.NetworkManager;
import com.jaimemartz.playerbalancer.manager.PasteHelper;
import com.jaimemartz.playerbalancer.manager.PlayerLocker;
import com.jaimemartz.playerbalancer.ping.StatusManager;
import com.jaimemartz.playerbalancer.section.SectionManager;
import com.jaimemartz.playerbalancer.services.FallbackService;
import com.jaimemartz.playerbalancer.services.MessagingService;
import com.jaimemartz.playerbalancer.settings.SettingsHolder;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.bstats.bungeecord.Metrics;
import org.bstats.bungeecord.Metrics.SingleLineChart;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.logging.Level;

public class PlayerBalancer extends Plugin {
    private boolean failed = false;
    private StatusManager statusManager;
    private SettingsHolder settings;
    private SectionManager sectionManager;
    private NetworkManager networkManager;
    private ConfigurationLoader<CommentedConfigurationNode> loader;
    private FallbackService fallbackService;
    private Command mainCommand, manageCommand;
    private MessagingService messagingService;
    private Listener connectListener, kickListener, reloadListener;

    @Override
    public void onEnable() {
        Metrics metrics = new Metrics(this);
        metrics.addCustomChart(new SingleLineChart("configured_sections",
                () -> sectionManager.getSections().size()
        ));

        if (!checkUpToDate()) {
            getLogger().info("You are using a version of PlayerBalancer that is not the latest on spigot");
            getLogger().info("You might want to update to benefit of new features, improvements and fixes");
            getLogger().info("Access the plugin page at https://www.spigotmc.org/resources/10788");
        }

        this.execStart();
    }

    public boolean checkUpToDate() {
        try {
            URLConnection con = new URL("https://api.spigotmc.org/legacy/update.php?resource=10788").openConnection();
            String reply = new BufferedReader(new InputStreamReader(con.getInputStream())).readLine();
            return getDescription().getVersion().equals(reply);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public void onDisable() {
        //Nothing else to do than normal stop
        this.execStop();
    }

    private void execStart() {
        if (!getDataFolder().exists())
            getDataFolder().mkdir();

        File file = new File(getDataFolder(), "plugin.conf");

        if (!file.exists()) {
            try (InputStream in = getResourceAsStream("default.conf")) {
                Files.copy(in, file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (loader == null) {
            loader = HoconConfigurationLoader.builder().setFile(file).build();
        }

        try {
            CommentedConfigurationNode node = loader.load();
            settings = node.getValue(TypeToken.of(SettingsHolder.class));

            mainCommand = new MainCommand(this);
            getProxy().getPluginManager().registerCommand(this, mainCommand);

            if (settings.getGeneralProps().isEnabled()) {
                if (settings.getGeneralProps().isSilent()) {
                    getLogger().setLevel(Level.WARNING);
                }

                if (settings.getGeneralProps().isAutoReload()) {
                    reloadListener = new ProxyReloadListener(this);
                    getProxy().getPluginManager().registerListener(this, reloadListener);
                }

                networkManager = new NetworkManager(this);

                sectionManager = new SectionManager(this);
                sectionManager.load();

                statusManager = new StatusManager(this);

                if (settings.getServerCheckerProps().isEnabled()) {
                    statusManager.start();
                }

                fallbackService = new FallbackService(this, settings.getFallbackCommandProps().getCommand());

                if (settings.getFallbackCommandProps().isEnabled()) {
                    getProxy().getPluginManager().registerCommand(this, fallbackService);
                }

                connectListener = new ServerConnectListener(this);
                getProxy().getPluginManager().registerListener(this, connectListener);

                if (settings.getGeneralProps().isPluginMessaging()) {
                    getProxy().registerChannel("PlayerBalancer");
                    messagingService = new MessagingService(this);
                    getProxy().getPluginManager().registerListener(this, fallbackService);
                    getProxy().getPluginManager().registerListener(this, messagingService);
                }

                manageCommand = new ManageCommand(this);
                getProxy().getPluginManager().registerCommand(this, manageCommand);

                getProxy().getPluginManager().registerListener(this, new PlayerDisconnectListener(this));

                PasteHelper.reset();

                if (settings.getKickHandlerProps().isEnabled()) {
                    kickListener = new ServerKickListener(this);
                    getProxy().getPluginManager().registerListener(this, kickListener);
                }

                getLogger().info("The plugin has finished loading without any problems");
            } else {
                getLogger().warning("-----------------------------------------------------");
                getLogger().warning("WARNING: This plugin is disabled, do not forget to set enabled on the config to true");
                getLogger().warning("Nothing is going to work until you do that, you can reload me by using the /balancer command");
                getLogger().warning("-----------------------------------------------------");
            }
        } catch (Exception e) {
            this.failed = true;
            getLogger().severe("The plugin could not continue loading due to an unexpected exception");
            e.printStackTrace();
        }
    }

    private void execStop() {
        getProxy().getPluginManager().unregisterCommand(mainCommand);
        mainCommand = null;

        if (settings.getGeneralProps().isEnabled()) {
            //Do not try to do anything if the plugin has not loaded correctly
            if (failed) return;

            if (settings.getGeneralProps().isAutoReload()) {
                getProxy().getPluginManager().unregisterListener(reloadListener);
                reloadListener = null;
            }

            if (settings.getServerCheckerProps().isEnabled()) {
                statusManager.stop();
            }

            if (settings.getFallbackCommandProps().isEnabled()) {
                getProxy().getPluginManager().unregisterCommand(fallbackService);
            }

            getProxy().getPluginManager().unregisterListener(fallbackService);

            fallbackService = null;

            if (settings.getKickHandlerProps().isEnabled()) {
                getProxy().getPluginManager().unregisterListener(kickListener);
                kickListener = null;
            }

            getProxy().getPluginManager().unregisterListener(connectListener);
            connectListener = null;

            if (settings.getGeneralProps().isPluginMessaging()) {
                getProxy().unregisterChannel("PlayerBalancer");
                getProxy().getPluginManager().unregisterListener(messagingService);
                messagingService = null;
            }

            getProxy().getPluginManager().unregisterCommand(manageCommand);
            manageCommand = null;

            sectionManager.flush();

            ServerAssignRegistry.getTable().clear();
        }

        PlayerLocker.flush();
        failed = false;
    }

    public boolean reloadPlugin() {
        getLogger().info("Reloading the plugin...");
        long starting = System.currentTimeMillis();

        this.execStop();
        this.execStart();

        if (!failed) {
            long ending = System.currentTimeMillis() - starting;
            getLogger().info(String.format("The plugin has been reloaded, took %sms", ending));
        }

        return !failed;
    }

    public SettingsHolder getSettings() {
        return settings;
    }

    public SectionManager getSectionManager() {
        return sectionManager;
    }

    public StatusManager getStatusManager() {
        return statusManager;
    }

    public NetworkManager getNetworkManager() {
        return networkManager;
    }
}
