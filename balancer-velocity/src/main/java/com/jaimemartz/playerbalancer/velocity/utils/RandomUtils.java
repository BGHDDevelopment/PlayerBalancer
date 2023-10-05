package com.jaimemartz.playerbalancer.velocity.utils;

import java.security.SecureRandom;
import java.util.List;

public final class RandomUtils {
    private static final SecureRandom instance = new SecureRandom();

    public static <T> T random(List<T> list) {
        return list.get(instance.nextInt(list.size()));
    }

    private RandomUtils() {}
}
