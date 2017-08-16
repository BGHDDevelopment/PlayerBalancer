package com.jaimemartz.playerbalancer.settings.types;

import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.properties.Property;
import com.jaimemartz.playerbalancer.connection.ProviderType;
import com.jaimemartz.playerbalancer.settings.beans.CommandData;
import com.jaimemartz.playerbalancer.settings.beans.SectionData;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static ch.jalu.configme.properties.PropertyInitializer.newStringKeyMapProperty;

public class SectionsHolder implements SettingsHolder {
    public static final Property<Map<String, SectionData>> SECTIONS = newStringKeyMapProperty(SectionData.class, "sections", getDefaultSections());

    public static Map<String, SectionData> getDefaultSections() {
        Map<String, SectionData> map = new HashMap<>();

        SectionData authLobbies = new SectionData();
        authLobbies.setProvider(ProviderType.RANDOM);
        authLobbies.setServers(Arrays.asList("Auth1", "Auth2", "Auth3"));
        map.put("auth-lobbies", authLobbies);

        SectionData generalLobbies = new SectionData();
        generalLobbies.setParent("auth-lobbies");
        generalLobbies.setPrincipal(true);
        generalLobbies.setProvider(ProviderType.RANDOM);
        generalLobbies.setServers(Arrays.asList("SWLobby1", "SWLobby2", "SWLobby3"));
        map.put("general-lobbies", generalLobbies);

        SectionData skywarsLobbies = new SectionData();
        skywarsLobbies.setParent("general-lobbies");
        skywarsLobbies.setProvider(ProviderType.LOWEST);
        skywarsLobbies.setServers(Arrays.asList("SWLobby1", "SWLobby2", "SWLobby3"));
        map.put("skywars-lobbies", skywarsLobbies);

        SectionData skywarsGames = new SectionData();
        skywarsGames.setProvider(ProviderType.FILLER);
        skywarsGames.setParent("skywarsd-lobbies");
        skywarsGames.setServers(Arrays.asList("SW_1", "SW2", "SW3", "SW4", "SW5"));
        skywarsGames.setCommand(new CommandData("playskywars", "", "skywars"));
        skywarsGames.setServer("skywars");
        skywarsGames.setDummy(true);
        map.put("skywars-games", skywarsGames);

        return map;
    }
}
