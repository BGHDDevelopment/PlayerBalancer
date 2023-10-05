package com.jaimemartz.playerbalancer.velocity.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;

@Slf4j
public final class ServerListPing {

    private static GsonComponentSerializer gson = GsonComponentSerializer.gson();

    private static int readVarInt(DataInputStream in) throws IOException {
        int i = 0;
        int j = 0;
        while (true) {
            int k = in.readByte();
            i |= (k & 0x7F) << j++ * 7;
            if (j > 5) throw new RuntimeException("VarInt too big");
            if ((k & 0x80) != 128) break;
        }
        return i;
    }

    private static void writeVarInt(DataOutputStream out, int paramInt) throws IOException {
        while (true) {
            if ((paramInt & 0xFFFFFF80) == 0) {
                out.writeByte(paramInt);
                return;
            }

            out.writeByte(paramInt & 0x7F | 0x80);
            paramInt >>>= 7;
        }
    }

    public StatusResponse ping(InetSocketAddress host, int timeout) throws IOException {
        try (Socket socket = new Socket()) {
            socket.setSoTimeout(timeout);
            socket.connect(host, timeout);

            try (DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
                 DataInputStream dataInputStream = new DataInputStream(socket.getInputStream())) {
                ByteArrayOutputStream b = new ByteArrayOutputStream();
                DataOutputStream handshake = new DataOutputStream(b);
                handshake.writeByte(0x00); // packet id for handshake
                writeVarInt(handshake, 4); // protocol version
                writeVarInt(handshake, host.getHostString().length()); // host length
                handshake.writeBytes(host.getHostString()); // host string
                handshake.writeShort(host.getPort()); // port
                writeVarInt(handshake, 1); // state (1 for handshake)

                writeVarInt(dataOutputStream, b.size()); // prepend size
                dataOutputStream.write(b.toByteArray()); // write handshake packet


                dataOutputStream.writeByte(0x01); // size is only 1
                dataOutputStream.writeByte(0x00); // packet id for ping
                int size = readVarInt(dataInputStream); // size of packet
                int id = readVarInt(dataInputStream); // packet id

                if (id == -1) {
                    throw new IOException("Premature end of stream.");
                }

                if (id != 0x00) { // we want a status response
                    throw new IOException("Invalid packetID");
                }

                int length = readVarInt(dataInputStream); // length of json string
                if (length == -1) {
                    throw new IOException("Premature end of stream.");
                }

                if (length == 0) {
                    throw new IOException("Invalid string length.");
                }

                byte[] in = new byte[length];
                dataInputStream.readFully(in);  // read json string
                String json = new String(in);


                long now = System.currentTimeMillis();
                dataOutputStream.writeByte(0x09); // size of packet
                dataOutputStream.writeByte(0x01); // 0x01 for ping
                dataOutputStream.writeLong(now); // time!?

                readVarInt(dataInputStream);
                id = readVarInt(dataInputStream);
                if (id == -1) {
                    throw new IOException("Premature end of stream.");
                }

                if (id != 0x01) {
                    throw new IOException("Invalid packetID");
                }

                long pingTime = dataInputStream.readLong(); // read response

                StatusResponse response = StatusResponse.fromJson(json);

                response.time = (int) (now - pingTime);
                return response;
            }
        }
    }

    public static class StatusResponse {
        private Component description;
        private Players players;
        private Version version;
        private String favicon;
        private int time;

        public Component getDescription() {
            return description;
        }

        public Players getPlayers() {
            return players;
        }

        public Version getVersion() {
            return version;
        }

        public String getFavicon() {
            return favicon;
        }

        public int getTime() {
            return time;
        }

        public static class Players {
            private int max;
            private int online;
            private List<Player> sample;

            public int getMax() {
                return max;
            }

            public int getOnline() {
                return online;
            }

            public List<Player> getSample() {
                return sample;
            }
        }

        public static class Player {
            private String name;
            private String id;

            public String getName() {
                return name;
            }

            public String getId() {
                return id;
            }
        }

        public static class Version {
            private String name;
            private String protocol;

            public String getName() {
                return name;
            }

            public String getProtocol() {
                return protocol;
            }
        }

        public static StatusResponse fromJson(String json) {
            StatusResponse response = new StatusResponse();

            JsonObject jsonObject = new JsonParser().parse(json).getAsJsonObject();

            // Extract and set the "description" field as an Adventure Component
            JsonElement descriptionElement = jsonObject.get("description");
            if (descriptionElement != null) {
                response.description = gson.deserialize(descriptionElement.toString());
            }

            // Extract and set the "players" field
            JsonObject playersObject = jsonObject.getAsJsonObject("players");
            if (playersObject != null) {
                response.players = new Players();
                response.players.max = playersObject.get("max").getAsInt();
                response.players.online = playersObject.get("online").getAsInt();
                // You can also extract the "sample" field if needed
            }

            // Extract and set the "version" field
            JsonObject versionObject = jsonObject.getAsJsonObject("version");
            if (versionObject != null) {
                response.version = new Version();
                response.version.name = versionObject.get("name").getAsString();
                response.version.protocol = versionObject.get("protocol").getAsString();
            }

            // Extract and set the "favicon" field
            JsonElement faviconElement = jsonObject.get("favicon");
            if (faviconElement != null) {
                response.favicon = faviconElement.getAsString();
            }

            return response;
        }
    }
}
