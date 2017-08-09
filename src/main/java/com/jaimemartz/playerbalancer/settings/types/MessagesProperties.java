package com.jaimemartz.playerbalancer.settings.types;

import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.properties.Property;

import java.util.Optional;

import static ch.jalu.configme.properties.PropertyInitializer.optionalStringProperty;

public class MessagesProperties implements SettingsHolder {
    public static final Property<Optional<String>> CONNECTING_SERVER = optionalStringProperty("settings.messages.connecting-server");

    public static final Property<Optional<String>> CONNECTED_SERVER = optionalStringProperty("settings.messages.connected-server");

    public static final Property<Optional<String>> MISC_FAILURE = optionalStringProperty("settings.messages.misc-failure");

    public static final Property<Optional<String>> UNKNOWN_SECTION = optionalStringProperty("settings.messages.unknown-section");

    public static final Property<Optional<String>> INVALID_INPUT = optionalStringProperty("settings.messages.invalid-input");

    public static final Property<Optional<String>> UNAVAILABLE_SERVER = optionalStringProperty("settings.messages.unavailable-server");

    public static final Property<Optional<String>> PLAYER_KICKED = optionalStringProperty("settings.messages.player-kicked");

    public static final Property<Optional<String>> PLAYER_BYPASS = optionalStringProperty("settings.messages.player-bypass");

    public static final Property<Optional<String>> SAME_SECTION = optionalStringProperty("settings.messages.same-section");
}
