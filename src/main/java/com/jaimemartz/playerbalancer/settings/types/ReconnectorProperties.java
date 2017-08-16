package com.jaimemartz.playerbalancer.settings.types;

import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.properties.Property;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static ch.jalu.configme.properties.PropertyInitializer.*;

public class ReconnectorProperties implements SettingsHolder {
    public static final Property<Boolean> ENABLED = newProperty("settings.reconnect-kick.enabled", true);

    public static final Property<Boolean> INVERTED = newProperty("settings.reconnect-kick.inverted", false);

    public static final Property<List<String>> REASONS = newListProperty("settings.reconnect-kick.reasons");

    public static final Property<List<String>> IGNORED_SECTIONS = newListProperty("settings.reconnect-kick.ignored");

    public static final Property<Boolean> RESTRICTED = newProperty("settings.reconnect-kick.restricted", true);

    public static final Property<Boolean> FORCE_PRINCIPAL = newProperty("settings.reconnect-kick.force-principal", false);

    public static final Property<Map<String, String>> RULES = newStringKeyMapProperty(String.class, "settings.reconnect-kick.rules",
            Collections.emptyMap()//ImmutableMap.of("section-from", "section-to")
    );

    public static final Property<Boolean> DEBUG = newProperty("settings.reconnect-kick.debug", false);
}
