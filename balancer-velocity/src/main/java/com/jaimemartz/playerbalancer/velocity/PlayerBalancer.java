package com.jaimemartz.playerbalancer.velocity;

import com.google.common.reflect.TypeToken;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.inject.Inject;
import com.jaimemartz.playerbalancer.velocity.commands.FallbackCommand;
import com.jaimemartz.playerbalancer.velocity.commands.MainCommand;
import com.jaimemartz.playerbalancer.velocity.commands.ManageCommand;
import com.jaimemartz.playerbalancer.velocity.connection.ServerAssignRegistry;
import com.jaimemartz.playerbalancer.velocity.helper.NetworkManager;
import com.jaimemartz.playerbalancer.velocity.helper.PasteHelper;
import com.jaimemartz.playerbalancer.velocity.helper.PlayerLocker;
import com.jaimemartz.playerbalancer.velocity.listeners.PlayerDisconnectListener;
import com.jaimemartz.playerbalancer.velocity.listeners.PluginMessageListener;
import com.jaimemartz.playerbalancer.velocity.listeners.ProxyReloadListener;
import com.jaimemartz.playerbalancer.velocity.listeners.ServerConnectListener;
import com.jaimemartz.playerbalancer.velocity.listeners.ServerKickListener;
import com.jaimemartz.playerbalancer.velocity.ping.StatusManager;
import com.jaimemartz.playerbalancer.velocity.section.SectionManager;
import com.jaimemartz.playerbalancer.velocity.settings.SettingsHolder;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.messages.LegacyChannelIdentifier;
import lombok.Getter;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.bstats.charts.SingleLineChart;
import org.bstats.velocity.Metrics;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

@Plugin(
        id = "playerbalancer",
        name = "PlayerBalancer Velocity",
        version = "2.3.3",
        description = "PlayerBalancer is a plugin for setting up a network with multiple lobbies of different types.",
        authors = {"jaime29010", "BGHDDevelopment", "HappyAreaBean"},
        dependencies = {
                @Dependency(id = "redisbungee", optional = true)
        }
)
@Getter
public class PlayerBalancer {
    private boolean failed = false;
    private StatusManager statusManager;
    private SettingsHolder settings;
    private SectionManager sectionManager;
    private NetworkManager networkManager;
    private ConfigurationLoader<CommentedConfigurationNode> loader;

    private FallbackCommand fallbackCommand;
    private SimpleCommand mainCommand, manageCommand;
    private CommandMeta mainCommandMeta, manageCommandMeta, fallbackCommandMeta;
    private Object connectListener, kickListener, reloadListener, pluginMessageListener;

    public static final LegacyChannelIdentifier PB_CHANNEL = new LegacyChannelIdentifier("playerbalancer:main");

    private final ProxyServer proxyServer;
    private final Logger logger;
    private final Metrics.Factory metricsFactory;
    private final PluginContainer container;
    private final Path dataDirectory;
    private String version = "2.3.4";

    @Inject
    public PlayerBalancer(ProxyServer proxyServer, Logger logger, Metrics.Factory metricsFactory, PluginContainer container, @DataDirectory Path dataDirectory) {
        this.proxyServer = proxyServer;
        this.logger = logger;
        this.metricsFactory = metricsFactory;
        this.container = container;
        this.dataDirectory = dataDirectory;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        Metrics metrics = metricsFactory.make(this, 1636);
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
            Optional<String> pluginVersion = container.getDescription().getVersion();
            if (!pluginVersion.isPresent()) return;
            String urlString = "https://updatecheck.bghddevelopment.com";
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String input;
            StringBuilder response = new StringBuilder();
            while ((input = reader.readLine()) != null) {
                response.append(input);
            }
            reader.close();
            JsonObject object = new JsonParser().parse(response.toString()).getAsJsonObject();

            if (object.has("plugins")) {
                JsonObject plugins = object.get("plugins").getAsJsonObject();
                JsonObject info = plugins.get("PlayerBalancer").getAsJsonObject();
                String version = info.get("version").getAsString();
                if (version.equals(getVersion())) {
                    getLogger().info(("PlayerBalancer is on the latest version."));
                } else {
                    getLogger().warn("");
                    getLogger().warn("");
                    getLogger().warn("Your PlayerBalancer version is out of date!");
                    getLogger().warn("We recommend updating ASAP!");
                    getLogger().warn("");
                    getLogger().warn("Your Version: " + pluginVersion.get());
                    getLogger().warn("Newest Version: " + version);
                    getLogger().warn("");
                    getLogger().warn("");
                }
            } else {
                logger.error("Wrong response from update API, contact plugin developer!");
            }
        } catch (Exception ex) {
            logger.error("Failed to get updater check. (" + ex.getMessage() + ")");
        }
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        this.execStop();
    }

    private void execStart() {
        if (!dataDirectory.toFile().exists())
            dataDirectory.toFile().mkdir();

        File file = new File(dataDirectory.toFile(), "plugin.conf");

        if (!file.exists()) {
            try (InputStream in = getClass().getResourceAsStream("velocity.conf")) {
                Files.copy(in, file.toPath());
            } catch (IOException e) {
                logger.error("Unable to copy velocity.conf", e);
            }
        }

        if (loader == null) {
            loader = HoconConfigurationLoader.builder().setFile(file).build();
        }

        try {
            CommandManager commandManager = proxyServer.getCommandManager();
            mainCommand = new MainCommand(this);
            mainCommandMeta = commandManager.metaBuilder("playerbalancer")
                    .aliases("balancer")
                    .plugin(this)
                    .build();
            commandManager.register(mainCommandMeta, mainCommand);

            CommentedConfigurationNode node = loader.load();
            settings = node.getValue(TypeToken.of(SettingsHolder.class));

            if (settings.getGeneralProps().isEnabled()) {
                if (settings.getGeneralProps().isAutoReload()) {
                    reloadListener = new ProxyReloadListener(this);
                    proxyServer.getEventManager().register(this, reloadListener);
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
                    fallbackCommandMeta = commandManager
                            .metaBuilder(settings.getFeaturesProps().getFallbackCommandProps().getCommand().getName())
                            .aliases(settings.getFeaturesProps().getFallbackCommandProps().getCommand().getAliasesArray())
                            .plugin(this)
                            .build();
                    commandManager.register(fallbackCommandMeta, fallbackCommand);
                }

                connectListener = new ServerConnectListener(this);
                proxyServer.getEventManager().register(this, connectListener);

                if (settings.getGeneralProps().isPluginMessaging()) {
                    proxyServer.getChannelRegistrar().register(PB_CHANNEL);

                    proxyServer.getEventManager().register(this, statusManager);

                    pluginMessageListener = new PluginMessageListener(this);
                    proxyServer.getEventManager().register(this, pluginMessageListener);
                }

                manageCommand = new ManageCommand(this);
                manageCommandMeta = commandManager
                        .metaBuilder("section")
                        .plugin(this)
                        .build();
                commandManager.register(manageCommandMeta, manageCommand);

                proxyServer.getEventManager().register(this, new PlayerDisconnectListener(this));

                if (settings.getFeaturesProps().getKickHandlerProps().isEnabled()) {
                    kickListener = new ServerKickListener(this);
                    proxyServer.getEventManager().register(this, kickListener);
                }

                PasteHelper.reset();
                getLogger().info("The plugin has finished loading without any problems");
            } else {
                getLogger().warn("-----------------------------------------------------");
                getLogger().warn("WARNING: This plugin is disabled, do not forget to set enabled on the config to true");
                getLogger().warn("Nothing is going to work until you do that, you can reload me by using the /balancer command");
                getLogger().warn("-----------------------------------------------------");
            }
        } catch (Exception e) {
            this.failed = true;
            getLogger().error("The plugin could not continue loading due to an unexpected exception", e);
        }
    }

    private void execStop() {
        if (mainCommand != null) {
            proxyServer.getCommandManager().unregister(mainCommandMeta);
            mainCommand = null;
        }

        if (settings.getGeneralProps().isEnabled()) {
            // Do not try to do anything if the plugin has not loaded correctly
            if (failed) return;

            if (settings.getGeneralProps().isAutoReload()) {
                if (reloadListener != null) {
                    proxyServer.getEventManager().unregisterListener(this, reloadListener);
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
                    proxyServer.getCommandManager().unregister(fallbackCommandMeta);
                    fallbackCommand = null;
                }
            }

            if (settings.getFeaturesProps().getKickHandlerProps().isEnabled()) {
                if (kickListener != null) {
                    proxyServer.getEventManager().unregisterListener(this, kickListener);
                    kickListener = null;
                }
            }

            if (connectListener != null) {
                proxyServer.getEventManager().unregisterListener(this, connectListener);
                connectListener = null;
            }

            if (settings.getGeneralProps().isPluginMessaging()) {
                if (pluginMessageListener != null) {
                    proxyServer.getChannelRegistrar().unregister(PB_CHANNEL);
                    proxyServer.getEventManager().unregisterListener(this, pluginMessageListener);
                    pluginMessageListener = null;
                }
            }

            if (manageCommand != null) {
                proxyServer.getCommandManager().unregister(manageCommandMeta);
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
