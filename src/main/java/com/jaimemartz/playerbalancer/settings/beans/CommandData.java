package com.jaimemartz.playerbalancer.settings.beans;

public final class CommandData {
    private String name;
    private String permission;
    private String[] aliases;

    public CommandData() {
        
    }

    public CommandData(String name, String permission, String... aliases) {
        this.name = name;
        this.permission = permission;
        this.aliases = aliases;
    }

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

    public String[] getAliases() {
        return aliases;
    }

    public void setAliases(String[] aliases) {
        this.aliases = aliases;
    }
}
