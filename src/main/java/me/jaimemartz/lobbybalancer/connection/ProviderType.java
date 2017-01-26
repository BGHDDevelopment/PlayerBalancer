package me.jaimemartz.lobbybalancer.connection;

import com.google.common.collect.Iterables;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CountryResponse;
import com.maxmind.geoip2.record.Country;
import me.jaimemartz.lobbybalancer.LobbyBalancer;
import me.jaimemartz.lobbybalancer.configuration.ConfigEntries;
import me.jaimemartz.lobbybalancer.ping.ServerStatus;
import me.jaimemartz.lobbybalancer.section.ServerSection;
import me.jaimemartz.lobbybalancer.utils.ConfigUtils;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static me.jaimemartz.lobbybalancer.LobbyBalancer.getPlayerCount;

public enum ProviderType {
    NONE(0, "Returns no server") {
        @Override
        public ServerInfo requestTarget(LobbyBalancer plugin, ServerSection section, List<ServerInfo> list, ProxiedPlayer player) {
            return null;
        }
    },
    DIRECT(1, "Returns the only server in the list") {
        @Override
        public ServerInfo requestTarget(LobbyBalancer plugin, ServerSection section, List<ServerInfo> list, ProxiedPlayer player) {
            return Iterables.getOnlyElement(list);
        }
    },
    LOCALIZED(2, "Returns the server that matches a region") {
        @Override
        public ServerInfo requestTarget(LobbyBalancer plugin, ServerSection section, List<ServerInfo> list, ProxiedPlayer player) {
            Configuration rules = plugin.getConfig().getSection("settings.geolocation.rules");
            if (ConfigEntries.GEOLOCATION_ENABLED.get() && ConfigUtils.isSet(rules, section.getName())) {
                Configuration rule = rules.getSection(section.getName());
                InetAddress address = player.getAddress().getAddress();

                try {
                    CountryResponse response = plugin.getGeolocationManager().getReader().country(address);
                    Country country = response.getCountry();

                    if (ConfigEntries.GEOLOCATION_PRINT_INFO.get()) {
                        plugin.getLogger().info(String.format(
                                "Player Address: \"%s\", Country Code: \"%s\"",
                                address.toString(),
                                country.getIsoCode()
                        ));
                    }

                    for (String name : rule.getKeys()) {
                        List<String> countries = rule.getStringList(name);
                        if (countries.contains(country.getIsoCode())) {
                            ServerInfo server = plugin.getProxy().getServerInfo(name);
                            if (server != null) {
                                return server;
                            }
                            break;
                        }
                    }
                } catch (IOException | GeoIp2Exception e) {
                    e.printStackTrace();
                }
            }
            return list.get(ThreadLocalRandom.current().nextInt(list.size()));
        }
    },
    LOWEST(3, "Returns the server with the least players online") {
        @Override
        public ServerInfo requestTarget(LobbyBalancer plugin, ServerSection section, List<ServerInfo> list, ProxiedPlayer player) {
            int min = Integer.MAX_VALUE;
            ServerInfo target = null;

            for (ServerInfo server : list) {
                int count = getPlayerCount(server);

                if (count < min) {
                    min = count;
                    target = server;
                }
            }

            return target;
        }
    },
    RANDOM(4, "Returns a random server") {
        @Override
        public ServerInfo requestTarget(LobbyBalancer plugin, ServerSection section, List<ServerInfo> list, ProxiedPlayer player) {
            return list.get(ThreadLocalRandom.current().nextInt(list.size()));
        }
    },
    PROGRESSIVE(5, "Returns the first server that is not full") {
        @Override
        public ServerInfo requestTarget(LobbyBalancer plugin, ServerSection section, List<ServerInfo> list, ProxiedPlayer player) {
            for (ServerInfo server : list) {
                ServerStatus status = plugin.getPingManager().getStatus(server);
                if (getPlayerCount(server) < status.getMaximumPlayers()) {
                    return server;
                }
            }

            return list.get(ThreadLocalRandom.current().nextInt(list.size()));
        }
    },
    FILLER(6, "Returns the server with the most players online that is not full") {
        @Override
        public ServerInfo requestTarget(LobbyBalancer plugin, ServerSection section, List<ServerInfo> list, ProxiedPlayer player) {
            int max = Integer.MIN_VALUE;
            ServerInfo target = null;

            for (ServerInfo server : list) {
                ServerStatus status = plugin.getPingManager().getStatus(server);
                int count = getPlayerCount(server);

                if (count > max && count <= status.getMaximumPlayers()) {
                    max = count;
                    target = server;
                }
            }

            return target;
        }
    };

    private final int id;
    private final String description;

    ProviderType(int id, String description) {
        this.id = id;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public abstract ServerInfo requestTarget(LobbyBalancer plugin, ServerSection section, List<ServerInfo> list, ProxiedPlayer player);
}