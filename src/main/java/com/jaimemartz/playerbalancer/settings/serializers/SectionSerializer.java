package com.jaimemartz.playerbalancer.settings.serializers;

import com.google.common.reflect.TypeToken;
import com.jaimemartz.playerbalancer.section.ServerSection;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;

public class SectionSerializer implements TypeSerializer<ServerSection> {
    @Override
    public ServerSection deserialize(TypeToken<?> typeToken, ConfigurationNode configurationNode) throws ObjectMappingException {
        return null;
    }

    @Override
    public void serialize(TypeToken<?> typeToken, ServerSection section, ConfigurationNode configurationNode) throws ObjectMappingException {

    }
}
