package com.jaimemartz.playerbalancer.settings.beans;

import lombok.Data;

import java.util.List;

@Data
public final class CommandBean {
    private final String name;
    private final String permission;
    private final List<String> aliases;

    public CommandBean() {
        this.name = null;
        this.permission = null;
        this.aliases = null;
    }

    public CommandBean(String name, String permission, List<String> aliases) {
        this.name = name;
        this.permission = permission;
        this.aliases = aliases;
    }
}
