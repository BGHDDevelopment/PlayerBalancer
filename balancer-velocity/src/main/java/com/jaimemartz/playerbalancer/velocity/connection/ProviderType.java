package com.jaimemartz.playerbalancer.velocity.connection;

import com.jaimemartz.playerbalancer.velocity.PlayerBalancer;
import com.jaimemartz.playerbalancer.velocity.connection.provider.AbstractProvider;
import com.jaimemartz.playerbalancer.velocity.connection.provider.types.NullProvider;
import com.jaimemartz.playerbalancer.velocity.connection.provider.types.progressive.ProgressiveFillerProvider;
import com.jaimemartz.playerbalancer.velocity.connection.provider.types.progressive.ProgressiveLowestProvider;
import com.jaimemartz.playerbalancer.velocity.connection.provider.types.progressive.ProgressiveProvider;
import com.jaimemartz.playerbalancer.velocity.connection.provider.types.random.RandomFillerProvider;
import com.jaimemartz.playerbalancer.velocity.connection.provider.types.random.RandomLowestProvider;
import com.jaimemartz.playerbalancer.velocity.connection.provider.types.random.RandomProvider;
import com.jaimemartz.playerbalancer.velocity.section.ServerSection;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import java.util.List;

public enum ProviderType {
    NONE {
        NullProvider provider = new NullProvider();

        @Override
        public RegisteredServer requestTarget(PlayerBalancer plugin, ServerSection section, List<RegisteredServer> servers, Player player) {
            return provider.requestTarget(plugin, section, servers, player);
        }
    },

    RANDOM {
        RandomProvider provider = new RandomProvider();

        @Override
        public RegisteredServer requestTarget(PlayerBalancer plugin, ServerSection section, List<RegisteredServer> servers, Player player) {
            return provider.requestTarget(plugin, section, servers, player);
        }
    },

    RANDOM_LOWEST {
        RandomLowestProvider provider = new RandomLowestProvider();

        @Override
        public RegisteredServer requestTarget(PlayerBalancer plugin, ServerSection section, List<RegisteredServer> servers, Player player) {
            return provider.requestTarget(plugin, section, servers, player);
        }
    },

    RANDOM_FILLER {
        RandomFillerProvider provider = new RandomFillerProvider();

        @Override
        public RegisteredServer requestTarget(PlayerBalancer plugin, ServerSection section, List<RegisteredServer> servers, Player player) {
            return provider.requestTarget(plugin, section, servers, player);
        }
    },

    PROGRESSIVE {
        ProgressiveProvider provider = new ProgressiveProvider();

        @Override
        public RegisteredServer requestTarget(PlayerBalancer plugin, ServerSection section, List<RegisteredServer> servers, Player player) {
            return provider.requestTarget(plugin, section, servers, player);
        }
    },

    PROGRESSIVE_LOWEST {
        ProgressiveLowestProvider provider = new ProgressiveLowestProvider();

        @Override
        public RegisteredServer requestTarget(PlayerBalancer plugin, ServerSection section, List<RegisteredServer> servers, Player player) {
            return provider.requestTarget(plugin, section, servers, player);
        }
    },

    PROGRESSIVE_FILLER {
        ProgressiveFillerProvider provider = new ProgressiveFillerProvider();

        @Override
        public RegisteredServer requestTarget(PlayerBalancer plugin, ServerSection section, List<RegisteredServer> servers, Player player) {
            return provider.requestTarget(plugin, section, servers, player);
        }
    },

    EXTERNAL {
        @Override
        public RegisteredServer requestTarget(PlayerBalancer plugin, ServerSection section, List<RegisteredServer> servers, Player player) {
            AbstractProvider provider = section.getExternalProvider();
            if (provider == null) {
                plugin.getLogger().warn("Target requested to the EXTERNAL provider with the section not having a provider instance, falling back to RANDOM...");
                return RANDOM.requestTarget(plugin, section, servers, player);
            }
            return provider.requestTarget(plugin, section, servers, player);
        }
    };

    public abstract RegisteredServer requestTarget(
            PlayerBalancer plugin,
            ServerSection section,
            List<RegisteredServer> servers,
            Player player
    );
}