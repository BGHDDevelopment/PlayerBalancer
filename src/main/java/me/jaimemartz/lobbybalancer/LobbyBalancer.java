package me.jaimemartz.lobbybalancer;

import com.google.gson.Gson;
import com.imaginarycode.minecraft.redisbungee.RedisBungee;
import me.jaimemartz.faucet.ConfigFactory;
import me.jaimemartz.lobbybalancer.commands.RegressCommand;
import me.jaimemartz.lobbybalancer.commands.MainCommand;
import me.jaimemartz.lobbybalancer.configuration.ConfigEntries;
import me.jaimemartz.lobbybalancer.connection.ServerAssignRegistry;
import me.jaimemartz.lobbybalancer.listener.*;
import me.jaimemartz.lobbybalancer.ping.PingManager;
import me.jaimemartz.lobbybalancer.section.SectionManager;
import me.jaimemartz.lobbybalancer.manager.AdapterFix;
import me.jaimemartz.lobbybalancer.manager.GeolocationManager;
import me.jaimemartz.lobbybalancer.manager.PlayerLocker;
import me.jaimemartz.lobbybalancer.utils.DigitUtils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import org.inventivetalent.update.bungee.BungeeUpdater;

import java.io.IOException;

public class LobbyBalancer extends Plugin {
    public static final String USER_ID = "%%__USER__%%";
    public static final String RESOURCE_ID = "%%__RESOURCE__%%";
    public static final String NONCE_ID = "%%__NONCE__%%";

    public static final int LAST_CONFIG_UPDATE_VER = 20200;

    private boolean failed = false;
    private Gson gson;

    private ConfigFactory factory;
    private PingManager pingManager;
    private SectionManager sectionManager;
    private Command regressCommand, mainCommand;
    private GeolocationManager geolocationManager;
    private Listener connectListener, kickListener, messageListener, reloadListener;

    @Override
    public void onEnable() {
        instance = this;
        gson = new Gson();

        if (factory == null) {
            factory = new ConfigFactory(this);
            factory.register(0, "config.yml");
            factory.submit(ConfigEntries.class);
        }

        factory.load(0, true);

        int configVersion = DigitUtils.getDigits(ConfigEntries.CONFIG_VERSION.get(), 5);
        if (configVersion < LAST_CONFIG_UPDATE_VER) {
            throw new IllegalStateException("Your config is outdated, please reset it and configure it again");
        } else {
            this.enable();
        }
    }

    private void enable() {
        mainCommand = new MainCommand(this);
        getProxy().getPluginManager().registerCommand(this, mainCommand);

        if (ConfigEntries.AUTO_RELOAD_ENABLED.get()) {
            reloadListener = new ProxyReloadListener(this);
            getProxy().getPluginManager().registerListener(this, reloadListener);
        }

        if (ConfigEntries.PLUGIN_ENABLED.get()) {
            if (ConfigEntries.CHECK_UPDATES_ENABLED.get()) {
                try {
                    new BungeeUpdater(this, 10788);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            sectionManager = new SectionManager(this);

            try {
                sectionManager.load();

                if (ConfigEntries.SERVER_CHECK_ENABLED.get()) {
                    pingManager = new PingManager(this);
                    pingManager.start();
                }

                if (ConfigEntries.REGRESS_COMMAND_ENABLED.get()) {
                    regressCommand = new RegressCommand(this);
                    getProxy().getPluginManager().registerCommand(this, regressCommand);
                }

                connectListener = new ServerConnectListener(this);
                getProxy().getPluginManager().registerListener(this, connectListener);

                messageListener = new PluginMessageListener(this);
                getProxy().getPluginManager().registerListener(this, messageListener);

                getProxy().getPluginManager().registerListener(this, new PlayerDisconnectListener(this));

                getProxy().registerChannel("LobbyBalancer");

                if (ConfigEntries.RECONNECT_KICK_ENABLED.get()) {
                    kickListener = new ServerKickListener(this);
                    getProxy().getPluginManager().registerListener(this, kickListener);
                }

                if (ConfigEntries.GEOLOCATION_ENABLED.get()) {
                    LobbyBalancer.printStartupInfo("The geolocation feature has not been tested in depth");
                    try {
                        geolocationManager = new GeolocationManager(this);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                getLogger().info("The plugin has finished loading without any problems");
            } catch (RuntimeException e) {
                failed = true;
                getLogger().severe("The plugin could not continue loading due to an unexpected exception");
                e.printStackTrace();
            }
        } else {
            getLogger().warning("The plugin is disabled, so nothing will work except the main command");
        }
    }

    @Override
    public void onDisable() {
        disable();
    }

    private void disable() {
        getProxy().getPluginManager().unregisterCommand(mainCommand);
        mainCommand = null;

        if (ConfigEntries.AUTO_RELOAD_ENABLED.get()) {
            getProxy().getPluginManager().unregisterListener(reloadListener);
            reloadListener = null;
        }

        if (ConfigEntries.PLUGIN_ENABLED.get()) {
            //Do not try to do anything if the plugin has not loaded correctly
            if (hasFailed()) return;

            if (ConfigEntries.SERVER_CHECK_ENABLED.get()) {
                pingManager.stop();
            }

            if (ConfigEntries.REGRESS_COMMAND_ENABLED.get()) {
                getProxy().getPluginManager().unregisterCommand(regressCommand);
                regressCommand = null;
            }

            getProxy().getPluginManager().unregisterListener(connectListener);
            connectListener = null;

            getProxy().getPluginManager().unregisterListener(messageListener);
            messageListener = null;

            if (ConfigEntries.RECONNECT_KICK_ENABLED.get()) {
                getProxy().getPluginManager().unregisterListener(kickListener);
                kickListener = null;
            }

            sectionManager.flush();
            AdapterFix.getFakeServers().clear();

            if (ConfigEntries.ASSIGN_TARGETS_ENABLED.get()) {
                ServerAssignRegistry.getTable().clear();
            }
        }
        PlayerLocker.flush();
        failed = false;
    }

    public void reloadPlugin() {
        printStartupInfo("Reloading the plugin...");
        long starting = System.currentTimeMillis();

        this.disable();
        factory.load(0, true);
        this.enable();

        long ending = System.currentTimeMillis() - starting;
        printStartupInfo("The plugin has been reloaded, took %sms", ending);
    }

    public static int getPlayerCount(ServerInfo server) {
        if (ConfigEntries.REDIS_BUNGEE_ENABLED.get()) {
            try {
                RedisBungee.getApi().getPlayersOnServer(server.getName()).size();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return server.getPlayers().size();
    }

    public static void checkSendMessage(CommandSender sender, String message) {
        if (message != null) {
            sender.sendMessage(TextComponent.fromLegacyText(message));
        }
    }

    public static boolean printStartupInfo(String format, Object... args) {
        if (ConfigEntries.SILENT_STARTUP.get()) {
            return false;
        }

        instance.getLogger().info(String.format(format, args));
        return true;
    }

    public Gson getGson() {
        return gson;
    }

    public GeolocationManager getGeolocationManager() {
        return geolocationManager;
    }

    public PingManager getPingManager() {
        return pingManager;
    }

    public SectionManager getSectionManager() {
        return sectionManager;
    }

    public boolean hasFailed() {
        return failed;
    }

    public Configuration getConfig() {
        return factory.get(0).getHandle();
    }

    private static LobbyBalancer instance;
    public static LobbyBalancer getInstance() {
        return instance;
    }
}
