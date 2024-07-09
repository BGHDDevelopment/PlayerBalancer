package com.jaimemartz.playerbalanceraddon;

import com.google.common.collect.Iterables;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

public class PluginMessageManager implements PluginMessageListener {
    private static final String PB_CHANNEL = "playerbalancer:main",
                                BC_CHANNEL = "bungeecord:main";

    private final Multimap<MessageContext, Consumer<ByteArrayDataInput>> contexts = LinkedHashMultimap.create();
    private final PlayerBalancerAddon plugin;

    public PluginMessageManager(PlayerBalancerAddon plugin) {
        this.plugin = plugin;

        plugin.getServer().getMessenger().registerIncomingPluginChannel(plugin, PB_CHANNEL, this);
        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, PB_CHANNEL);

        // In case we need to use BungeeCord channels
        plugin.getServer().getMessenger().registerIncomingPluginChannel(plugin, BC_CHANNEL, this);
        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, BC_CHANNEL);
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (channel.equals(PB_CHANNEL)) {
            ByteArrayDataInput in = ByteStreams.newDataInput(message);
            String subchannel = in.readUTF();

            Collection<Consumer<ByteArrayDataInput>> consumers = contexts.get(
                    new MessageContext(channel, subchannel, player.getUniqueId())
            );

            if (consumers != null) {
                synchronized (consumers) {
                    Iterator<Consumer<ByteArrayDataInput>> iterator = consumers.iterator();
                    if (iterator.hasNext()) {
                        iterator.next().accept(in);
                        iterator.remove();
                    }
                }
            }
        }
    }

    public void connectPlayer(Player player, String section) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(section);
        player.sendPluginMessage(plugin, PB_CHANNEL, out.toByteArray());
    }

    public void fallbackPlayer(Player player) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("FallbackPlayer");
        out.writeUTF(player.getName());
        player.sendPluginMessage(plugin, PB_CHANNEL, out.toByteArray());
    }

    public boolean getSectionByName(String section, Consumer<String> consumer) {
        Player player = Iterables.getFirst(plugin.getServer().getOnlinePlayers(), null);
        if (player == null) {
            return false;
        }

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("GetSectionByName");
        out.writeUTF(section);

        contexts.put(new MessageContext(
                PB_CHANNEL,
                "GetSectionByName",
                player.getUniqueId()
        ), (response) -> consumer.accept(response.readUTF()));

        player.sendPluginMessage(plugin, PB_CHANNEL, out.toByteArray());
        return true;
    }

    public boolean getSectionByServer(String server, Consumer<String> consumer) {
        Player player = Iterables.getFirst(plugin.getServer().getOnlinePlayers(), null);
        if (player == null) {
            return false;
        }

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("GetSectionByServer");
        out.writeUTF(server);

        contexts.put(new MessageContext(
                PB_CHANNEL,
                "GetSectionByServer",
                player.getUniqueId()
        ), (response) -> consumer.accept(response.readUTF()));

        player.sendPluginMessage(plugin, PB_CHANNEL, out.toByteArray());
        return true;
    }

    public void getSectionOfPlayer(Player player, Consumer<String> consumer) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("GetSectionOfPlayer");
        out.writeUTF(player.getName());

        contexts.put(new MessageContext(
                PB_CHANNEL,
                "GetSectionOfPlayer",
                player.getUniqueId()
        ), (response) -> consumer.accept(response.readUTF()));

        player.sendPluginMessage(plugin, PB_CHANNEL, out.toByteArray());
    }

    public boolean getServerStatus(String server, Consumer<String> consumer) {
        Player player = Iterables.getFirst(plugin.getServer().getOnlinePlayers(), null);
        if (player == null) {
            return false;
        }

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("GetServerStatus");
        out.writeUTF(player.getName());

        contexts.put(new MessageContext(
                PB_CHANNEL,
                "GetServerStatus",
                player.getUniqueId()
        ), (response) -> consumer.accept(response.readUTF()));

        player.sendPluginMessage(plugin, PB_CHANNEL, out.toByteArray());
        return true;
    }

    public boolean getSectionPlayerCount(String section, Consumer<Integer> consumer) {
        Player player = Iterables.getFirst(plugin.getServer().getOnlinePlayers(), null);
        if (player == null) {
            return false;
        }

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("GetSectionPlayerCount");
        out.writeUTF(section);

        contexts.put(new MessageContext(
                PB_CHANNEL,
                "GetSectionPlayerCount",
                player.getUniqueId()
        ), (response) -> consumer.accept(response.readInt()));

        player.sendPluginMessage(plugin, PB_CHANNEL, out.toByteArray());
        return true;
    }

    public void clearPlayerBypass(Player player) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("ClearPlayerBypass");
        player.sendPluginMessage(plugin, PB_CHANNEL, out.toByteArray());
    }

    public void setPlayerBypass(Player player) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("SetPlayerBypass");
        player.sendPluginMessage(plugin, PB_CHANNEL, out.toByteArray());
    }

    public void bypassConnect(Player player, String server) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("BypassConnect");
        out.writeUTF(server);
        player.sendPluginMessage(plugin, PB_CHANNEL, out.toByteArray());
    }

    public boolean clearStatusOverride(String server) {
        Player player = Iterables.getFirst(plugin.getServer().getOnlinePlayers(), null);
        if (player == null) {
            return false;
        }

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("ClearStatusOverride");
        out.writeUTF(server);
        player.sendPluginMessage(plugin, PB_CHANNEL, out.toByteArray());

        return true;
    }

    public boolean setStatusOverride(String server, boolean status) {
        Player player = Iterables.getFirst(plugin.getServer().getOnlinePlayers(), null);
        if (player == null) {
            return false;
        }

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("SetStatusOverride");
        out.writeUTF(server);
        out.writeBoolean(status);
        player.sendPluginMessage(plugin, PB_CHANNEL, out.toByteArray());

        return true;
    }

    private static final class MessageContext {
        private final String channel;
        private final String subchannel;
        private final UUID player;

        public MessageContext(String channel, String subchannel, UUID player) {
            this.channel = channel;
            this.subchannel = subchannel;
            this.player = player;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            MessageContext that = (MessageContext) o;
            return Objects.equals(channel, that.channel) &&
                    Objects.equals(subchannel, that.subchannel) &&
                    Objects.equals(player, that.player);
        }

        @Override
        public int hashCode() {
            return Objects.hash(channel, subchannel, player);
        }

        @Override
        public String toString() {
            return "MessageContext{" +
                    "channel='" + channel + '\'' +
                    ", subchannel='" + subchannel + '\'' +
                    ", player=" + player +
                    '}';
        }
    }
}
