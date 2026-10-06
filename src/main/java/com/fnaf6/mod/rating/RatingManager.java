package com.fnaf6.mod.rating;

import com.fnaf6.mod.Fnaf6Mod;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class RatingManager {
    private static final Map<UUID, Double> RATINGS = new HashMap<>();

    private RatingManager() {}

    public static void register() {
        Fnaf6Mod.LOGGER.debug("Registered rating manager");
    }

    public static void applyAnimatronicBonus(ServerPlayerEntity player, double entertainment, double atmosphere, double safety, double revenue) {
        double rating = RATINGS.getOrDefault(player.getUuid(), 50.0D);
        double next = Math.max(0.0D, Math.min(100.0D, rating + ((entertainment + atmosphere + revenue + safety) * 5.0D)));
        RATINGS.put(player.getUuid(), next);
        Fnaf6Mod.LOGGER.debug("Rating updated for {} to {}", player.getName().getString(), next);
    }

    public static double getRating(ServerPlayerEntity player) {
        return RATINGS.getOrDefault(player.getUuid(), 50.0D);
    }
}
