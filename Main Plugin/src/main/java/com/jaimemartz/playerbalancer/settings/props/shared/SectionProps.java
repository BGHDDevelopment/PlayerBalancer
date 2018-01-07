package com.jaimemartz.playerbalancer.settings.props.shared;

import com.jaimemartz.playerbalancer.connection.ProviderType;
import lombok.Data;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.List;

@ConfigSerializable
@Data
public class SectionProps {
    @Setting
    private ProviderType provider;

    @Setting
    private String alias;

    @Setting(value = "parent")
    private String parentName;

    @Setting(value = "servers")
    private List<String> serverEntries;

    @Setting(value = "section-command")
    private CommandProps commandProps;

    @Setting(value = "section-server")
    private String serverName;
}
