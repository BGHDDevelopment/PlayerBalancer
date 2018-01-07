package com.jaimemartz.playerbalancer;

import com.google.common.reflect.TypeToken;
import com.jaimemartz.playerbalancer.commands.*;
import com.jaimemartz.playerbalancer.connection.ServerAssignRegistry;
import com.jaimemartz.playerbalancer.helper.NetworkManager;
import com.jaimemartz.playerbalancer.helper.PasteHelper;
import com.jaimemartz.playerbalancer.helper.PlayerLocker;
import com.jaimemartz.playerbalancer.listeners.*;
import com.jaimemartz.playerbalancer.ping.StatusManager;
import com.jaimemartz.playerbalancer.section.SectionManager;
import com.jaimemartz.playerbalancer.settings.SettingsHolder;
import com.jaimemartz.playerbalancer.utils.CustomFormatter;
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
import java.util.concurrent.TimeUnit;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class PlayerBalancer extends Plugin {
    private boolean failed = false;
    private StatusManager statusManager;
    private SettingsHolder settings;
    private SectionManager sectionManager;
    private NetworkManager networkManager;
    private ConfigurationLoader<CommentedConfigurationNode> loader;
    private final StringBuilder logsBuilder = new StringBuilder();

    private FallbackCommand fallbackCommand;
    private Command mainCommand, manageCommand, findCommand, listCommand, serverCommand;
    private Listener connectListener, kickListener, reloadListener, pluginMessageListener;

    @Override
    public void onLoad() {
        Handler handler = new Handler() {
            @Override
            public void publish(LogRecord record) {
                if (isLoggable(record)) {
                    String formatted = getFormatter().format(record);
                    logsBuilder.append(formatted);
                }
            }

            @Override
            public void flush() {
                logsBuilder.setLength(0);
            }

            @Override
            public void close() throws SecurityException {
                //Nothing to do
            }
        };

        handler.setFormatter(new CustomFormatter());
        getProxy().getLogger().addHandler(handler);
        getProxy().getLogger().setUseParentHandlers(true);
    }

    @Override
    public void onEnable() {
        Metrics metrics = new Metrics(this);
        metrics.addCustomChart(new SingleLineChart("configured_sections", () -> {
            if (sectionManager != null) {
                return sectionManager.getSections().size();
            } else {
                return 0;
            }
        }));

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
            mainCommand = new MainCommand(this);
            getProxy().getPluginManager().registerCommand(this, mainCommand);

            CommentedConfigurationNode node = loader.load();
            settings = node.getValue(TypeToken.of(SettingsHolder.class));

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

                if (settings.getFeaturesProps().getServerCheckerProps().isEnabled()) {
                    statusManager.start();
                }

                if (settings.getFeaturesProps().getFallbackCommandProps().isEnabled()) {
                    fallbackCommand = new FallbackCommand(this);
                    getProxy().getPluginManager().registerCommand(this, fallbackCommand);
                }

                connectListener = new ServerConnectListener(this);
                getProxy().getPluginManager().registerListener(this, connectListener);

                if (settings.getGeneralProps().isPluginMessaging()) {
                    getProxy().registerChannel("PlayerBalancer");

                    getProxy().getPluginManager().registerListener(this, statusManager);

                    pluginMessageListener = new PluginMessageListener(this);
                    getProxy().getPluginManager().registerListener(this, pluginMessageListener);
                }

                manageCommand = new ManageCommand(this);
                getProxy().getPluginManager().registerCommand(this, manageCommand);

                getProxy().getPluginManager().registerListener(this, new PlayerDisconnectListener(this));

                if (settings.getFeaturesProps().getKickHandlerProps().isEnabled()) {
                    kickListener = new ServerKickListener(this);
                    getProxy().getPluginManager().registerListener(this, kickListener);
                }

                //After the modules have loaded (hopefully?)
                getProxy().getScheduler().schedule(this, () -> {
                    if (settings.getFeaturesProps().getCustomFindCommandProps().isEnabled()) {
                        tryUnregisterCommands("cmd_find");
                        findCommand = new CustomFindCommand(this);
                        getProxy().getPluginManager().registerCommand(this, findCommand);
                    }

                    if (settings.getFeaturesProps().getCustomListCommandProps().isEnabled()) {
                        tryUnregisterCommands("cmd_list");
                        listCommand = new CustomListCommand(this);
                        getProxy().getPluginManager().registerCommand(this, listCommand);
                    }

                    if (settings.getFeaturesProps().getCustomServerCommandProps().isEnabled()) {
                        tryUnregisterCommands("cmd_server");
                        serverCommand = new CustomServerCommand(this);
                        getProxy().getPluginManager().registerCommand(this, serverCommand);
                    }

                }, 5L, TimeUnit.SECONDS);

                PasteHelper.reset();
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

    private void tryUnregisterCommands(String pluginName) {
        Plugin plugin = getProxy().getPluginManager().getPlugin(pluginName);
        if (plugin != null) {
            getProxy().getPluginManager().unregisterCommands(plugin);
            getLogger().info("Unregistered all commands of the plugin: " + pluginName);
        } else {
            getLogger().warning("Could not find the plugin: " + pluginName);
        }
    }

    private void execStop() {
        if (mainCommand != null) {
            getProxy().getPluginManager().unregisterCommand(mainCommand);
            mainCommand = null;
        }

        if (settings.getGeneralProps().isEnabled()) {
            //Do not try to do anything if the plugin has not loaded correctly
            if (failed) return;

            if (settings.getGeneralProps().isAutoReload()) {
                if (reloadListener != null) {
                    getProxy().getPluginManager().unregisterListener(reloadListener);
                    reloadListener = null;
                }
            }

            if (settings.getFeaturesProps().getServerCheckerProps().isEnabled()) {
                if (statusManager != null) {
                    statusManager.stop();
                }
            }

            if (settings.getFeaturesProps().getFallbackCommandProps().isEnabled()) {
                if (fallbackCommand != null) {
                    getProxy().getPluginManager().unregisterCommand(fallbackCommand);
                    fallbackCommand = null;
                }
            }

            if (settings.getFeaturesProps().getKickHandlerProps().isEnabled()) {
                if (kickListener != null) {
                    getProxy().getPluginManager().unregisterListener(kickListener);
                    kickListener = null;
                }
            }

            if (connectListener != null) {
                getProxy().getPluginManager().unregisterListener(connectListener);
                connectListener = null;
            }

            if (settings.getGeneralProps().isPluginMessaging()) {
                if (pluginMessageListener != null) {
                    getProxy().unregisterChannel("PlayerBalancer");
                    getProxy().getPluginManager().unregisterListener(pluginMessageListener);
                    pluginMessageListener = null;
                }
            }

            if (manageCommand != null) {
                getProxy().getPluginManager().unregisterCommand(manageCommand);
                manageCommand = null;
            }

            if (settings.getFeaturesProps().getCustomFindCommandProps().isEnabled()) {
                if (findCommand != null) {
                    getProxy().getPluginManager().unregisterCommand(findCommand);
                    findCommand = null;
                }
            }

            if (settings.getFeaturesProps().getCustomListCommandProps().isEnabled()) {
                if (listCommand != null) {
                    getProxy().getPluginManager().unregisterCommand(listCommand);
                    listCommand = null;
                }
            }

            if (settings.getFeaturesProps().getCustomServerCommandProps().isEnabled()) {
                if (serverCommand != null) {
                    getProxy().getPluginManager().unregisterCommand(serverCommand);
                    serverCommand = null;
                }
            }

            if (sectionManager != null) {
                sectionManager.flush();
            }

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

    public FallbackCommand getFallbackCommand() {
        return fallbackCommand;
    }

    public StringBuilder getLogsBuilder() {
        return logsBuilder;
    }
}
