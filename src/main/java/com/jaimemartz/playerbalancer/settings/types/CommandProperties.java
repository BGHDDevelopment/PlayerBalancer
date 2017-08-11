package com.jaimemartz.playerbalancer.settings.types;

import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.properties.Property;
import com.jaimemartz.playerbalancer.settings.beans.CommandBean;

import java.util.Arrays;
import java.util.List;

import static ch.jalu.configme.properties.PropertyInitializer.*;

public class CommandProperties implements SettingsHolder {
    public static final Property<Boolean> ENABLED = newProperty("settings.fallback-command.enabled", true);

    public static final Property<CommandBean> COMMAND = newBeanProperty(CommandBean.class, "settings.fallback-command.command",
            new CommandBean("fallback", "", Arrays.asList("lobby", "hub", "back"))
    );

    public static final Property<List<String>> IGNORED_SECTIONS = newListProperty("settings.fallback-command.ignored");

    public static final Property<Boolean> RESTRICTED = newProperty("settings.fallback-command.restricted", true);

    /*
    public static final Property<MapBean> RULES = newBeanProperty(MapBean.class, "settings.reconnect-kick",
            new MapBean(ImmutableMap.of("section-from", "section-to"))
    );
    */
}
