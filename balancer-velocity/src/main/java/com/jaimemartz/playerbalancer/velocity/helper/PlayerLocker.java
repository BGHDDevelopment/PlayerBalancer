package com.jaimemartz.playerbalancer.velocity.helper;

import com.velocitypowered.api.proxy.Player;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PlayerLocker {
    private static final Set<UUID> storage = Collections.synchronizedSet(new HashSet<UUID>());

    public static boolean lock(Player player) {
        if (storage.contains(player.getUniqueId())) {
            return false;
        } else {
            storage.add(player.getUniqueId());
            return true;
        }
    }

    public static boolean unlock(Player player) {
        if (storage.contains(player.getUniqueId())) {
            storage.remove(player.getUniqueId());
            return true;
        } else {
            return false;
        }
    }

    public static boolean isLocked(Player player) {
        return storage.contains(player.getUniqueId());
    }

    public static void flush() {
        storage.clear();
    }
}
