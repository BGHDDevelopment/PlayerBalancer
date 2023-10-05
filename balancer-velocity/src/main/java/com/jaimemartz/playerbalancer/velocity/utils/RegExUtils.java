package com.jaimemartz.playerbalancer.velocity.utils;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class RegExUtils {
    private static final LoadingCache<String, Pattern> COMPILED_PATTERNS = CacheBuilder.newBuilder().build(new CacheLoader<String, Pattern>() {
        @Override
        public Pattern load(String string) throws Exception {
            return Pattern.compile(string);
        }
    });

    public static Pattern getPattern(String regexp) {
        try {
            return COMPILED_PATTERNS.get(regexp);
        } catch (ExecutionException e) {
            throw new RuntimeException("Error while getting a pattern from the cache");
        }
    }

    public static boolean matches(String string, String expression) {
        return getMatcher(string, expression).matches();
    }

    private static Matcher getMatcher(String string, String expression) {
        Pattern pattern = getPattern(expression);
        return pattern.matcher(string);
    }

    private RegExUtils() {}
}
