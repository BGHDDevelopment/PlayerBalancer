package me.jaimemartz.lobbybalancer.section;

import com.google.gson.annotations.Expose;
import me.jaimemartz.lobbybalancer.LobbyBalancer;
import me.jaimemartz.lobbybalancer.connection.ProviderType;
import me.jaimemartz.lobbybalancer.utils.AlphanumComparator;
import me.jaimemartz.lobbybalancer.utils.FixedAdapter;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.config.Configuration;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ServerSection {
    private final LobbyBalancer plugin;
    private Configuration configuration;
    private List<ServerInfo> sortedServers;

    @Expose private final String name;
    @Expose private boolean principal;
    @Expose private int position;
    @Expose private boolean dummy;
    @Expose private ServerSection parent;
    @Expose private boolean inherited = false;
    @Expose private List<ServerInfo> servers;
    @Expose private ProviderType provider;
    @Expose private ServerInfo server;
    @Expose private SectionCommand command;
    @Expose private boolean valid = false;

    public ServerSection(LobbyBalancer plugin, String name, Configuration configuration) {
        this.plugin = plugin;
        this.name = name;
        this.configuration = configuration;
        this.servers = new ArrayList<>();
    }

    public ServerSection(LobbyBalancer plugin, String name, boolean principal, int position, boolean dummy, ServerSection parent, boolean inherited, List<ServerInfo> servers, ProviderType provider, ServerInfo server, SectionCommand command, boolean valid) {
        this.plugin = plugin;
        this.configuration = null;
        this.name = name;
        this.principal = principal;
        this.position = position;
        this.dummy = dummy;
        this.parent = parent;
        this.inherited = inherited;
        this.servers = servers;
        this.provider = provider;
        this.server = server;
        this.command = command;
        this.valid = valid;
    }

    public void preInit() {
        checkInit();

        if (configuration == null) {
            throw new IllegalStateException("Tried to call an init method with null configuration section");
        }

        principal = configuration.getBoolean("principal", false);

        if (principal) {
            ServerSection section = plugin.getSectionManager().getPrincipal();
            if (section != null) {
                throw new IllegalStateException(String.format("The section \"%s\" is already principal", section.getName()));
            } else {
                plugin.getSectionManager().setPrincipal(this);
            }
        }

        dummy = configuration.getBoolean("dummy", false);

        if (configuration.contains("parent")) {
            parent = plugin.getSectionManager().getByName(configuration.getString("parent"));

            if (parent == null) {
                throw new IllegalArgumentException(String.format("The section \"%s\" has an invalid parent set", name));
            }
        }

        if (configuration.contains("servers")) {
            configuration.getStringList("servers").forEach(entry -> {
                Pattern pattern = Pattern.compile(entry);
                AtomicBoolean matches = new AtomicBoolean(false);
                plugin.getProxy().getServers().forEach((key, value) -> {
                    Matcher matcher = pattern.matcher(key);
                    if (matcher.matches()) {
                        plugin.getLogger().info(String.format("Found a match with \"%s\" for entry \"%s\"", key, entry));
                        servers.add(value);
                        plugin.getSectionManager().register(value, this);
                        matches.set(true);
                    }
                });

                if (!matches.get()) {
                    plugin.getLogger().warning(String.format("Could not match a server with the entry \"%s\"", entry));
                }
            });

            plugin.getLogger().info(String.format("Recognized %s server(s) out of %s entries on the section \"%s\"", servers.size(), configuration.getStringList("servers").size(), this.name));
        } else {
            throw new IllegalArgumentException(String.format("The section \"%s\" does not have any servers set", name));
        }

    }

    public void load() {
        checkInit();

        if (configuration == null) {
            throw new IllegalStateException("Tried to call an init method with null configuration section");
        }

        if (parent != null && parent.parent == this) {
            throw new IllegalStateException(String.format("The sections \"%s\" and \"%s\" are parents of each other", this.name, parent.name));
        }

        if (configuration.contains("provider")) {
            try {
                provider = ProviderType.valueOf(configuration.getString("provider").toUpperCase());
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        } else {
            if (principal && parent == null) {
                throw new IllegalArgumentException(String.format("The principal section \"%s\" does not have a provider set", name));
            }
        }
    }

    public void postInit() {
        checkInit();

        if (configuration == null) {
            throw new IllegalStateException("Tried to call an init method with null configuration section");
        }

        Callable<Integer> callable = () -> {
            //Calculate above principal
            int iterations = 0;
            ServerSection current = this;
            while (current != null) {
                if (current.isPrincipal()) {
                    return iterations;
                }

                current = current.getParent();
                iterations++;
            }

            //Calculate below principal
            if (plugin.getSectionManager().hasPrincipal()) {
                iterations = 0;
                current = plugin.getSectionManager().getPrincipal();
                while (current != null) {
                    if (current.equals(this)) {
                        return iterations;
                    }

                    current = current.getParent();
                    iterations--;
                }
            }

            //Calculated iterations above parents
            return iterations;
        };

        try {
            position = callable.call();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (provider == null) {
            ServerSection sect = this.parent;
            if (sect != null) {
                while (sect.provider == null) {
                    sect = sect.parent;
                }

                plugin.getLogger().info(String.format("The section \"%s\" inherits the provider from the section \"%s\"", this.name, sect.name));
                provider = sect.provider;
                inherited = true;
            }
        }

        if (provider == null) {
            throw new IllegalStateException(String.format("The section \"%s\" does not have a provider", name));
        }

        if (configuration.contains("section-server")) {
            int port = (int) Math.floor(Math.random() * (0xFFFF + 1)); //Get a random valid port for our fake server
            server = plugin.getProxy().constructServerInfo("@" + configuration.getString("section-server"), new InetSocketAddress("0.0.0.0", port), String.format("Server of Section %s", name), false);
            plugin.getSectionManager().register(server, this);
            FixedAdapter.getFakeServers().put(server.getName(), server);
            plugin.getProxy().getServers().put(server.getName(), server);
        }

        if (configuration.contains("section-command")) {
            Configuration other = configuration.getSection("section-command");

            String name = other.getString("name");
            String permission = other.getString("permission");
            List<String> aliases = other.getStringList("aliases");

            command = new SectionCommand(plugin, name, permission, aliases, this);
            plugin.getProxy().getPluginManager().registerCommand(plugin, command);
        }

        sortedServers = new ArrayList<>();
        sortedServers.addAll(servers);
        sortedServers.sort(new AlphanumComparator());

        valid = true;
    }

    private void checkInit() {
        if (!valid) return;
        throw new IllegalStateException("Tried to init a section that is already valid");
    }

    public String getName() {
        return name;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public boolean isPrincipal() {
        return principal;
    }

    public int getPosition() {
        return position;
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
        return servers;
    }

    public List<ServerInfo> getSortedServers() {
        return sortedServers;
    }

    public ProviderType getProvider() {
        return provider;
    }

    public boolean hasInheritedProvider() {
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

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public void setPrincipal(boolean principal) {
        this.principal = principal;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void setDummy(boolean dummy) {
        this.dummy = dummy;
    }

    public void setParent(ServerSection parent) {
        this.parent = parent;
    }

    public void setInherited(boolean inherited) {
        this.inherited = inherited;
    }

    public void setServers(List<ServerInfo> servers) {
        this.servers = servers;
    }

    public void setSortedServers(List<ServerInfo> sortedServers) {
        this.sortedServers = sortedServers;
    }

    public void setProvider(ProviderType provider) {
        this.provider = provider;
    }

    public void setServer(ServerInfo server) {
        this.server = server;
    }

    public void setCommand(SectionCommand command) {
        this.command = command;
    }
}