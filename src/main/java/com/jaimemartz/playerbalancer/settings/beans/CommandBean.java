package com.jaimemartz.playerbalancer.settings.beans;

import lombok.Data;

import java.util.List;

@Data
public final class CommandBean {
    private String name;
    private String permission;
    private List<String> aliases;

    public CommandBean() {
        
    }

    public CommandBean(String name, String permission, List<String> aliases) {
        this.name = name;
        this.permission = permission;
        this.aliases = aliases;
    }
}
