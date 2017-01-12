package me.jaimemartz.lobbybalancer.manager;

import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PlayerLocker {
    private static final Set<UUID> storage = Collections.synchronizedSet(new HashSet<UUID>());

    public static boolean lock(ProxiedPlayer player) {
        if (storage.contains(player.getUniqueId())) {
            return false;
        } else {
            storage.add(player.getUniqueId());
            return true;
        }
    }

    public static boolean unlock(ProxiedPlayer player) {
        if (storage.contains(player.getUniqueId())) {
            storage.remove(player.getUniqueId());
            return true;
        } else {
            return false;
        }
    }

    public static boolean isLocked(ProxiedPlayer player) {
        return storage.contains(player.getUniqueId());
    }

    public static void flush() {
        storage.clear();
    }
}
