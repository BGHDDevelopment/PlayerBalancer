package me.jaimemartz.lobbybalancer.utils;

public class LevenshteinDistance {
    public static String getClosestString(String input, String[] options) {
        int lowestDistance = 10;
        String lowest = "";

        for (String string : options) {
            int distance = getLevenshteinDistance(input, string);
            if (distance < lowestDistance) {
                lowestDistance = distance;
                lowest = string;
            }
        }

        return lowest;
    }

    public static String getClosestString(String input, Iterable<String> options) {
        int lowestDistance = 10;
        String lowest = "";

        for (String string : options) {
            int distance = getLevenshteinDistance(input, string);
            if (distance < lowestDistance) {
                lowestDistance = distance;
                lowest = string;
            }
        }

        return lowest;
    }

    private static int getLevenshteinDistance(String s, String t) {
        if (s == null || t == null) {
            return 11;
        }

        int n = s.length();
        int m = t.length();

        if (n == 0) {
            return m;
        } else if (m == 0) {
            return n;
        }

        if (n > m) {
            String tmp = s;
            s = t;
            t = tmp;
            n = m;
            m = t.length();
        }

        int p[] = new int[n + 1];
        int d[] = new int[n + 1];
        int _d[];

        int i;
        int j;

        char t_j;

        int cost;

        for (i = 0; i <= n; i++) {
            p[i] = i;
        }

        for (j = 1; j <= m; j++) {
            t_j = t.charAt(j - 1);
            d[0] = j;

            for (i = 1; i <= n; i++) {
                cost = s.charAt(i - 1) == t_j ? 0 : 1;
                d[i] = Math.min(Math.min(d[i - 1] + 1, p[i] + 1), p[i - 1] + cost);
            }

            _d = p;
            p = d;
            d = _d;
        }

        return p[n];
    }
}