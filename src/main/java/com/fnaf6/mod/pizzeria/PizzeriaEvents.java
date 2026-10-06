package com.fnaf6.mod.pizzeria;

import com.fnaf6.mod.Fnaf6Mod;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class PizzeriaEvents {
    private static final Map<UUID, Double> PIZZERIA_SCORE = new HashMap<>();

    private PizzeriaEvents() {}

    public static void register() {
        Fnaf6Mod.LOGGER.debug("Registered pizzeria event layer");
    }

    public static void updateScore(ServerPlayerEntity player, double ratingDelta) {
        double current = PIZZERIA_SCORE.getOrDefault(player.getUuid(), 0.0D);
        double next = Math.max(0.0D, Math.min(100.0D, current + ratingDelta));
        PIZZERIA_SCORE.put(player.getUuid(), next);
        Fnaf6Mod.LOGGER.debug("Pizzeria score updated for {} to {}", player.getName().getString(), next);
    }

    public static double getScore(ServerPlayerEntity player) {
        return PIZZERIA_SCORE.getOrDefault(player.getUuid(), 0.0D);
    }
}
