package com.jaimemartz.playerbalancer.section;

import com.jaimemartz.playerbalancer.connection.ProviderType;
import com.jaimemartz.playerbalancer.settings.shared.SectionProps;
import net.md_5.bungee.api.config.ServerInfo;

import java.util.List;

public class ServerSection {
    private final String name;
    private int position;

    private final SectionProps props;

    private boolean inherited = false;
    private ServerSection parent;

    private ServerInfo server;
    private SectionCommand command;

    private List<ServerInfo> mappedServers;
    private List<ServerInfo> sortedServers;

    private boolean valid = false;

    public ServerSection(String name, SectionProps props) {
        this.name = name;
        this.props = props;
    }

    public void preInit() {
        /*
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
        */
    }

    public void load() {
        /*
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
        */
    }

    public void postInit() {
        /*
        if (configuration == null) {
            throw new IllegalStateException("Tried to call an init method with null configuration section");
        }

        try {
            position = calculatePosition();
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
        */
    }

    public String getName() {
        return name;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public SectionProps getProps() {
        return props;
    }

    public boolean isInherited() {
        return inherited;
    }

    public void setInherited(boolean inherited) {
        this.inherited = inherited;
    }

    public ServerSection getParent() {
        return parent;
    }

    public void setParent(ServerSection parent) {
        this.parent = parent;
    }

    public ProviderType getEffectiveProvider() {
        return inherited ? parent.getEffectiveProvider() : props.getProvider();
    }

    public void setProvider(ProviderType provider) {
        props.setProvider(provider);
        inherited = false;
    }

    public ServerInfo getServer() {
        return server;
    }

    public void setServer(ServerInfo server) {
        this.server = server;
    }

    public SectionCommand getCommand() {
        return command;
    }

    public void setCommand(SectionCommand command) {
        this.command = command;
    }

    public List<ServerInfo> getMappedServers() {
        return mappedServers;
    }

    public void setMappedServers(List<ServerInfo> mappedServers) {
        this.mappedServers = mappedServers;
    }

    public List<ServerInfo> getSortedServers() {
        return sortedServers;
    }

    public void setSortedServers(List<ServerInfo> sortedServers) {
        this.sortedServers = sortedServers;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }
}