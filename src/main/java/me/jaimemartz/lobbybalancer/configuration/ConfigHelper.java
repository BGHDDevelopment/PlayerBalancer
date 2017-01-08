package me.jaimemartz.lobbybalancer.configuration;

import me.jaimemartz.faucet.ConfigEntry;

import java.util.List;

public class ConfigHelper {
    public static Object get(ConfigEntry<Object> entry) {
        return entry.get();
    }

    public static String getString(ConfigEntry<String> entry) {
        return entry.get();
    }

    public static List<String> getStringList(ConfigEntry<List<String>> entry) {
        return entry.get();
    }

    public static boolean getBoolean(ConfigEntry<Boolean> entry) {
        return entry.get();
    }

    public static List<Boolean> getBooleanList(ConfigEntry<List<Boolean>> entry) {
        return entry.get();
    }

    public static byte getByte(ConfigEntry<Byte> entry) {
        return entry.get();
    }

    public static List<Byte> getByteList(ConfigEntry<List<Byte>> entry) {
        return entry.get();
    }

    public static char getChar(ConfigEntry<Character> entry) {
        return entry.get();
    }

    public static List<Character> getCharList(ConfigEntry<List<Character>> entry) {
        return entry.get();
    }

    public static double getDouble(ConfigEntry<Double> entry) {
        return entry.get();
    }

    public static List<Double> getDoubleList(ConfigEntry<List<Double>> entry) {
        return entry.get();
    }

    public static float getFloat(ConfigEntry<Float> entry) {
        return entry.get();
    }

    public static List<Float> getFloatList(ConfigEntry<List<Float>> entry) {
        return entry.get();
    }

    public static int getInt(ConfigEntry<Integer> entry) {
        return entry.get();
    }

    public static List<Integer> getIntList(ConfigEntry<List<Integer>> entry) {
        return entry.get();
    }

    public static long getLong(ConfigEntry<Long> entry) {
        return entry.get();
    }

    public static List<Long> getLongList(ConfigEntry<List<Long>> entry) {
        return entry.get();
    }

    public static short getShort(ConfigEntry<Short> entry) {
        return entry.get();
    }

    public static List<Short> getShortList(ConfigEntry<List<Short>> entry) {
        return entry.get();
    }
}
