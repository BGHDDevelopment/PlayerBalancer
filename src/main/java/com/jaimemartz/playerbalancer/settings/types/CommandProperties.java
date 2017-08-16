package com.jaimemartz.playerbalancer.settings.types;

import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.properties.Property;
import com.google.common.collect.ImmutableMap;
import com.jaimemartz.playerbalancer.settings.beans.CommandData;

import java.util.List;
import java.util.Map;

import static ch.jalu.configme.properties.PropertyInitializer.*;

public class CommandProperties implements SettingsHolder {
    public static final Property<Boolean> ENABLED = newProperty("settings.fallback-command.enabled", true);

    public static final Property<CommandData> COMMAND = newBeanProperty(CommandData.class, "settings.fallback-command.command",
            new CommandData("fallback", "", "lobby", "hub", "back")
    );

    public static final Property<List<String>> IGNORED_SECTIONS = newListProperty("settings.fallback-command.ignored");

    public static final Property<Boolean> RESTRICTED = newProperty("settings.fallback-command.restricted", true);

    public static final Property<Map<String, String>> RULES = newStringKeyMapProperty(String.class, "settings.fallback-command.rules",
            ImmutableMap.of("section-from", "section-to")
    );
}
