package com.jaimemartz.playerbalancer;

import com.google.common.reflect.TypeToken;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jaimemartz.playerbalancer.commands.FallbackCommand;
import com.jaimemartz.playerbalancer.commands.MainCommand;
import com.jaimemartz.playerbalancer.commands.ManageCommand;
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
import org.bstats.charts.SingleLineChart;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.util.logging.FileHandler;
import java.util.logging.Level;

public class PlayerBalancer extends Plugin {
    private boolean failed = false;
    private StatusManager statusManager;
    private SettingsHolder settings;
    private SectionManager sectionManager;
    private NetworkManager networkManager;
    private ConfigurationLoader<CommentedConfigurationNode> loader;

    private FallbackCommand fallbackCommand;
    private Command mainCommand, manageCommand;
    private Listener connectListener, kickListener, reloadListener, pluginMessageListener;

    public static final String LOG_FILE_PATTERN = "balancer.log";
    public static final String PB_CHANNEL = "playerbalancer:main";

    @Override
    public void onLoad() {
        try {
            FileHandler handler = new FileHandler(LOG_FILE_PATTERN);
            handler.setFormatter(new CustomFormatter());
            getProxy().getLogger().addHandler(handler);
            getProxy().getLogger().setUseParentHandlers(true);
        } catch (Exception e) {
            getLogger().log(Level.WARNING, "Could not set custom log handler", e);
        }
    }

    @Override
    public void onEnable() {
        Metrics metrics = new Metrics(this, 1636);
        metrics.addCustomChart(new SingleLineChart("configured_sections", () -> {
            if (sectionManager != null) {
                return sectionManager.getSections().size();
            } else {
                return 0;
            }
        }));

        updateCheck();

        this.execStart();
    }

    public void updateCheck() {
        try {
            String urlString = "https://updatecheck.bghddevelopment.com";
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String input;
            StringBuffer response = new StringBuffer();
            while ((input = reader.readLine()) != null) {
                response.append(input);
            }
            reader.close();
            JsonObject object = new JsonParser().parse(response.toString()).getAsJsonObject();

            if (object.has("plugins")) {
                JsonObject plugins = object.get("plugins").getAsJsonObject();
                JsonObject info = plugins.get("PlayerBalancer").getAsJsonObject();
                String version = info.get("version").getAsString();
                if (version.equals(getDescription().getVersion())) {
                    getLogger().log(Level.INFO, ("PlayerBalancer is on the latest version."));
                } else {
                    getLogger().log(Level.WARNING, (""));
                    getLogger().log(Level.WARNING, (""));
                    getLogger().log(Level.WARNING, ("Your PlayerBalancer version is out of date!"));
                    getLogger().log(Level.WARNING, ("We recommend updating ASAP!"));
                    getLogger().log(Level.WARNING, (""));
                    getLogger().log(Level.WARNING, ("Your Version: " + getDescription().getVersion()));
                    getLogger().log(Level.WARNING, ("Newest Version: " + version));
                    getLogger().log(Level.WARNING, (""));
                    getLogger().log(Level.WARNING, (""));
                    return;
                }
                return;
            } else {
                getLogger().log(Level.SEVERE, ("&cWrong response from update API, contact plugin developer!"));
                return;
            }
        } catch (
                Exception ex) {
            getLogger().log(Level.SEVERE, ("&cFailed to get updater check. (" + ex.getMessage() + ")"));
            return;
        }
    }

    @Override
    public void onDisable() {
        // Nothing else to do than normal stop
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
                    getProxy().registerChannel(PB_CHANNEL);

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
            // Do not try to do anything if the plugin has not loaded correctly
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
                    getProxy().unregisterChannel(PB_CHANNEL);
                    getProxy().getPluginManager().unregisterListener(pluginMessageListener);
                    pluginMessageListener = null;
                }
            }

            if (manageCommand != null) {
                getProxy().getPluginManager().unregisterCommand(manageCommand);
                manageCommand = null;
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

    public FallbackCommand getFallbackCommand() {
        return fallbackCommand;
    }

    public NetworkManager getNetworkManager() {
        return networkManager;
    }
}
