package com.jaimemartz.playerbalancer.settings.types;

import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.properties.Property;
import com.jaimemartz.playerbalancer.ping.PingTactic;

import java.util.List;

import static ch.jalu.configme.properties.PropertyInitializer.newListProperty;
import static ch.jalu.configme.properties.PropertyInitializer.newProperty;

public class CheckerProperties implements SettingsHolder {
    public static final Property<Boolean> ENABLED = newProperty("settings.server-check.enabled", true);

    public static final Property<PingTactic> TACTIC = newProperty(PingTactic.class, "settings.server-check.tactic", PingTactic.CUSTOM);

    public static final Property<Integer> ATTEMPTS = newProperty("settings.server-check.attempts", 5);

    public static final Property<Integer> INTERVAL = newProperty("settings.server-check.interval", 10000);

    public static final Property<Integer> TIMEOUT = newProperty("settings.server-check.timeout", 5000);

    public static final Property<Boolean> DEBUG = newProperty("settings.server-check.print-info", false);

    public static final Property<List<String>> MARKER_DESCS = newListProperty("settings.server-check.print-info",
            "Sever is not accessible",
            "Gamemode has already started"
    );
}
