package com.jaimemartz.playerbalancer.velocity.utils;

public final class DigitUtils {
    public static int getDigits(String string, int digits) {
        StringBuilder builder = new StringBuilder();

        for (char character : string.toCharArray()) {
            if (Character.isDigit(character)) {
                if (builder.length() >= digits) {
                    break;
                }

                builder.append(character);
            }
        }

        while (builder.length() < digits) {
            builder.append("0");
        }

        return Integer.parseInt(builder.toString());
    }

    public static int getDigits(String string) {
        StringBuilder builder = new StringBuilder();

        for (char character : string.toCharArray()) {
            if (Character.isDigit(character)) {
                builder.append(character);
            }
        }

        return Integer.parseInt(builder.toString());
    }

    private DigitUtils() {}
}
