package com.jaimemartz.playerbalancer.utils;

import com.google.gson.*;
import net.md_5.bungee.api.config.ServerInfo;

import java.lang.reflect.Type;

public class ServerInfoAdapter implements JsonSerializer<ServerInfo> {
    @Override
    public JsonElement serialize(ServerInfo server, Type type, JsonSerializationContext context) {
        return context.serialize(server.getName());
    }
}
