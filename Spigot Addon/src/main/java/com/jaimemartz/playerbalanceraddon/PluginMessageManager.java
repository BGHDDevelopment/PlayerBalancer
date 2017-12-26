package com.jaimemartz.playerbalanceraddon;

import com.google.common.collect.Iterables;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.util.Iterator;
import java.util.UUID;
import java.util.function.Consumer;

public class PluginMessageManager implements PluginMessageListener {
    private final Multimap<MessageContext, Consumer<ByteArrayDataInput>> contexts = LinkedHashMultimap.create();
    private final PlayerBalancerAddon plugin;

    public PluginMessageManager(PlayerBalancerAddon plugin) {
        this.plugin = plugin;

        plugin.getServer().getMessenger().registerIncomingPluginChannel(plugin, "PlayerBalancer", this);
        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, "PlayerBalancer");

        //In case we need to use BungeeCord channels
        plugin.getServer().getMessenger().registerIncomingPluginChannel(plugin, "BungeeCord", this);
        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, "BungeeCord");
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (channel.equals("PlayerBalancer")) {
            ByteArrayDataInput in = ByteStreams.newDataInput(message);
            String subchannel = in.readUTF();

            Iterator<Consumer<ByteArrayDataInput>> iterator = contexts.get(
                    new MessageContext(channel, subchannel, player.getUniqueId())
            ).iterator();

            if (iterator.hasNext()) {
                iterator.next().accept(in);
                iterator.remove();
            }
        }
    }

    public void connectPlayer(Player player, String section) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(section);
        player.sendPluginMessage(plugin, "PlayerBalancer", out.toByteArray());
    }

    public void fallbackPlayer(Player player) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("FallbackPlayer");
        out.writeUTF(player.getName());
        player.sendPluginMessage(plugin, "PlayerBalancer", out.toByteArray());
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
                "PlayerBalancer",
                "GetSectionByName",
                player.getUniqueId()
        ), (response) -> consumer.accept(response.readUTF()));

        player.sendPluginMessage(plugin, "PlayerBalancer", out.toByteArray());
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
                "PlayerBalancer",
                "GetSectionByServer",
                player.getUniqueId()
        ), (response) -> consumer.accept(response.readUTF()));

        player.sendPluginMessage(plugin, "PlayerBalancer", out.toByteArray());
        return true;
    }

    public void getSectionOfPlayer(Player player, Consumer<String> consumer) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("GetSectionOfPlayer");
        out.writeUTF(player.getName());

        contexts.put(new MessageContext(
                "PlayerBalancer",
                "GetSectionOfPlayer",
                player.getUniqueId()
        ), (response) -> consumer.accept(response.readUTF()));

        player.sendPluginMessage(plugin, "PlayerBalancer", out.toByteArray());
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
                "PlayerBalancer",
                "GetServerStatus",
                player.getUniqueId()
        ), (response) -> consumer.accept(response.readUTF()));

        player.sendPluginMessage(plugin, "PlayerBalancer", out.toByteArray());
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
                "PlayerBalancer",
                "GetSectionPlayerCount",
                player.getUniqueId()
        ), (response) -> consumer.accept(response.readInt()));

        player.sendPluginMessage(plugin, "PlayerBalancer", out.toByteArray());
        return true;
    }

    public void clearPlayerBypass(Player player) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("ClearPlayerBypass");
        player.sendPluginMessage(plugin, "PlayerBalancer", out.toByteArray());
    }

    public void setPlayerBypass(Player player) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("SetPlayerBypass");
        player.sendPluginMessage(plugin, "PlayerBalancer", out.toByteArray());
    }

    public void bypassConnect(Player player, String server) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("BypassConnect");
        out.writeUTF(server);
        player.sendPluginMessage(plugin, "PlayerBalancer", out.toByteArray());
    }

    public boolean clearStatusOverride(String server) {
        Player player = Iterables.getFirst(plugin.getServer().getOnlinePlayers(), null);
        if (player == null) {
            return false;
        }

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("ClearStatusOverride");
        out.writeUTF(server);
        player.sendPluginMessage(plugin, "PlayerBalancer", out.toByteArray());

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
        player.sendPluginMessage(plugin, "PlayerBalancer", out.toByteArray());

        return true;
    }

    private final class MessageContext {
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

            if (channel != null ? !channel.equals(that.channel) : that.channel != null) return false;
            if (subchannel != null ? !subchannel.equals(that.subchannel) : that.subchannel != null) return false;
            return player != null ? player.equals(that.player) : that.player == null;
        }

        @Override
        public int hashCode() {
            int result = channel != null ? channel.hashCode() : 0;
            result = 31 * result + (subchannel != null ? subchannel.hashCode() : 0);
            result = 31 * result + (player != null ? player.hashCode() : 0);
            return result;
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
