package com.jaimemartz.playerbalancer.velocity.utils;

public class BuildInfo {
    public static String getUserId() {
        return "%%__USER__%%";
    }

    public static String getResourceId() {
        return "%%__RESOURCE__%%";
    }

    public static String getNonceId() {
        return "%%__NONCE__%%";
    }
}
