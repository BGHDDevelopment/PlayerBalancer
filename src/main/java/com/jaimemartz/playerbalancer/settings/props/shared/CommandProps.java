package com.jaimemartz.playerbalancer.settings.props.shared;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.List;

@ConfigSerializable
public class CommandProps {
    @Setting
    private String name;

    @Setting
    private String permission;

    @Setting
    private List<String> aliases;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public List<String> getAliases() {
        return aliases;
    }

    public String[] getAliasesArray() {
        return aliases.toArray(new String[aliases.size()]);
    }

    public void setAliases(List<String> aliases) {
        this.aliases = aliases;
    }
}
