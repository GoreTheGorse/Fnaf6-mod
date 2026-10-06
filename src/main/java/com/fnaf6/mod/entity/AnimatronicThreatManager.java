package com.fnaf6.mod.entity;

import com.fnaf6.mod.Fnaf6Mod;
import com.fnaf6.mod.rating.RatingManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class AnimatronicThreatManager {
    private static final Map<UUID, Double> THREAT_LEVELS = new HashMap<>();

    private AnimatronicThreatManager() {}

    public static void register() {
        Fnaf6Mod.LOGGER.debug("Registered animatronic threat manager");
    }

    public static void updateThreat(ServerPlayerEntity player, int animatronicCount) {
        double threat = Math.min(100.0, animatronicCount * 12.5D);
        THREAT_LEVELS.put(player.getUuid(), threat);

        double entertainment = 1.0 + (threat / 100.0);
        double atmosphere = 1.0 + (threat / 200.0);
        double safety = Math.max(-20.0, -threat / 8.0);
        double revenue = 2.0 + (threat / 50.0);

        RatingManager.applyAnimatronicBonus(player, entertainment, atmosphere, safety, revenue);
    }

    public static double getThreat(PlayerEntity player) {
        return THREAT_LEVELS.getOrDefault(player.getUuid(), 0.0D);
    }
}
