package com.jaimemartz.playerbalancer.settings.props.shared;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.Collections;
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
        if (permission != null) {
            return permission;
        } else {
            return "";
        }
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public List<String> getAliases() {
        if (aliases != null) {
            return aliases;
        } else {
            return Collections.emptyList();
        }
    }

    public String[] getAliasesArray() {
        if (aliases != null) {
            return aliases.toArray(new String[aliases.size()]);
        } else {
            return new String[] {};
        }
    }

    public void setAliases(List<String> aliases) {
        this.aliases = aliases;
    }

    @Override
    public String toString() {
        return "CommandProps{" +
                "name='" + name + '\'' +
                ", permission='" + permission + '\'' +
                ", aliases=" + aliases +
                '}';
    }
}
