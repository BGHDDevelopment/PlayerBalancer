package com.jaimemartz.playerbalancer.velocity.settings.props.shared;

import lombok.Data;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.Collections;
import java.util.List;

@ConfigSerializable
@Data
public class CommandProps {
    @Setting
    private String name;

    @Setting
    private String permission;

    @Setting
    private List<String> aliases;

    public String getPermission() {
        if (permission != null) {
            return permission;
        } else {
            return "";
        }
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
}
