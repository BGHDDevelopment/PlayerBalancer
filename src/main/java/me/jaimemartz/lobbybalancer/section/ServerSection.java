package me.jaimemartz.lobbybalancer.section;

import me.jaimemartz.lobbybalancer.LobbyBalancer;
import me.jaimemartz.lobbybalancer.configuration.ConfigHelper;
import me.jaimemartz.lobbybalancer.connection.ProviderType;
import me.jaimemartz.lobbybalancer.utils.FixedAdapter;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.config.Configuration;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ServerSection {
    private transient final Configuration section;
    private transient final SectionManager manager;

    private final String name;
    private boolean principal;
    private boolean dummy;
    private ServerSection parent;
    private boolean inherited = false;
    private List<ServerInfo> servers;
    private ProviderType provider;
    private ServerInfo server;
    private SectionCommand command;
    private boolean valid = false;

    ServerSection(String name, Configuration section, SectionManager manager) {
        this.name = name;
        this.section = section;
        this.manager = manager;
        this.servers = new ArrayList<>();
    }

    void preInit(LobbyBalancer plugin) {
        principal = section.getBoolean("principal", false);

        if (principal) {
            ServerSection section = manager.getPrincipal();
            if (section != null) {
                throw new IllegalStateException(String.format("The section \"%s\" is already principal", section.getName()));
            } else {
                manager.setPrincipal(this);
            }
        }

        dummy = section.getBoolean("dummy", false);

        if (ConfigHelper.isSet(section, "parent")) {
            parent = manager.getByName(section.getString("parent"));

            if (parent == null) {
                throw new IllegalArgumentException(String.format("The section \"%s\" has an invalid parent set", name));
            }
        }

        if (ConfigHelper.isSet(section, "servers")) {
            section.getStringList("servers").forEach(entry -> {
                Pattern pattern = Pattern.compile(entry);
                AtomicBoolean matches = new AtomicBoolean(false);
                plugin.getProxy().getServers().forEach((key, value) -> {
                    Matcher matcher = pattern.matcher(key);
                    if (matcher.matches()) {
                        plugin.getLogger().info(String.format("Found a match with \"%s\" for entry \"%s\"", key, entry));
                        servers.add(value);
                        manager.register(value, this);
                        matches.set(true);
                    }
                });

                if (!matches.get()) {
                    plugin.getLogger().warning(String.format("Could not match a server with the entry \"%s\"", entry));
                }
            });

            plugin.getLogger().info(String.format("Recognized %s server(s) out of %s entries on the section \"%s\"", servers.size(), section.getStringList("servers").size(), this.name));
        } else {
            throw new IllegalArgumentException(String.format("The section \"%s\" does not have any servers set", name));
        }

    }

    void load(LobbyBalancer plugin) {
        if (parent != null && parent.parent == this) {
            throw new IllegalStateException(String.format("The section \"%s\" and \"%s\" are parents of each other", this.name, parent.name));
        }

        if (ConfigHelper.isSet(section, "provider")) {
            try {
                provider = ProviderType.valueOf(section.getString("provider").toUpperCase());
                if (provider == ProviderType.LOCALIZED) {
                    Configuration rules = plugin.getConfig().getSection("settings.geolocation.rules");
                    if (!ConfigHelper.isSet(rules, name)) {
                        throw new IllegalStateException(String.format("The section \"%s\" does not have a rule set in the geolocation section", this.name));
                    }
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        } else {
            if (principal) {
                throw new IllegalArgumentException(String.format("The principal section \"%s\" does not have a provider set", name));
            }
        }
    }

    void postInit(LobbyBalancer plugin) {
        if (provider == null) {
            ServerSection sect = this.parent;
            while (sect.provider == null) {
                sect = sect.parent;
            }

            plugin.getLogger().info(String.format("The section \"%s\" inherits the provider from the section \"%s\"", this.name, sect.name));
            provider = sect.provider;
            inherited = true;
        }

        if (provider == null) {
            throw new IllegalStateException(String.format("The section \"%s\" does not have a provider", name));
        }

        if (ConfigHelper.isSet(section, "section-server")) {
            int port = (int) Math.floor(Math.random() * (0xFFFF + 1)); //Get a random valid port for our fake server
            server = plugin.getProxy().constructServerInfo("@" + section.getString("section-server"), new InetSocketAddress("0.0.0.0", port), String.format("Server of Section %s", name), false);
            plugin.getSectionManager().register(server, this);
            FixedAdapter.getFakeServers().put(server.getName(), server);
            plugin.getProxy().getServers().put(server.getName(), server);
        }

        if (ConfigHelper.isSet(section, "section-command")) {
            Configuration other = section.getSection("section-command");

            String name = other.getString("name");
            String permission = other.getString("permission");
            List<String> aliases = other.getStringList("aliases");

            command = new SectionCommand(plugin, name, permission, aliases, this);
            plugin.getProxy().getPluginManager().registerCommand(plugin, command);
        }

        this.setValid(true);
    }

    public String getName() {
        return name;
    }

    protected Configuration getSection() {
        return section;
    }

    public boolean isPrincipal() {
        return principal;
    }

    public boolean isDummy() {
        return dummy;
    }

    public boolean hasParent() {
        return parent != null;
    }

    public ServerSection getParent() {
        return parent;
    }

    public List<ServerInfo> getServers() {
        return Collections.unmodifiableList(servers);
    }

    public ProviderType getProvider() {
        return provider;
    }

    public boolean isProviderInherited() {
        return inherited;
    }

    public ServerInfo getServer() {
        return server;
    }

    public SectionCommand getCommand() {
        return command;
    }

    public boolean hasServer() {
        return server != null;
    }

    public boolean hasCommand() {
        return command != null;
    }

    public boolean isValid() {
        return valid;
    }

    void setValid(boolean valid) {
        this.valid = valid;
    }
}