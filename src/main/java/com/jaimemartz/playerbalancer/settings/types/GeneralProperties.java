package com.jaimemartz.playerbalancer.settings.types;

import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.properties.Property;

import static ch.jalu.configme.properties.PropertyInitializer.newProperty;

public class GeneralProperties implements SettingsHolder {
    public static final Property<Boolean> ENABLED = newProperty("settings.enabled", false);

    public static final Property<Boolean> SILENT = newProperty("settings.silent", false);

    public static final Property<Boolean> AUTO_RELOAD = newProperty("settings.auto-reload", true);

    public static final Property<Boolean> REDIS_BUNGEE = newProperty("settings.redis-bungee", false);

    public static final Property<Boolean> ASSIGN_TARGETS = newProperty("settings.assign-targets", true);

    public static final Property<Boolean> FALLBACK_PRINCIPAL = newProperty("settings.fallback-principal", true);

    public static final Property<Boolean> AUTO_UPDATE = newProperty("settings.auto-update", false);
}
