package com.fnaf6.mod.rating;

import com.fnaf6.mod.Fnaf6Mod;
import com.fnaf6.mod.config.Fnaf6Config;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RatingManager {
    private static final Map<UUID, FazRating> RATINGS = new HashMap<>();

    public static void register() {
        Fnaf6Mod.LOGGER.debug("Registered rating manager");
    }

    public static FazRating getOrCreate(UUID playerId) {
        return RATINGS.computeIfAbsent(playerId, id -> new FazRating(50.0, 50.0, 50.0, 50.0));
    }

    public static FazRating get(UUID playerId) {
        return RATINGS.getOrDefault(playerId, new FazRating(50.0, 50.0, 50.0, 50.0));
    }

    public static void set(UUID playerId, FazRating rating) {
        RATINGS.put(playerId, rating);
    }

    public static void updateFromPizzeria(ServerPlayerEntity player, double entertainmentBonus, double atmosphereBonus,
                                          double safetyBonus, double revenueBonus) {
        if (!Fnaf6Config.get().enableRatingSystem) {
            return;
        }

        FazRating current = getOrCreate(player.getUuid());
        FazRating updated = new FazRating(
                current.entertainment + entertainmentBonus,
                current.atmosphere + atmosphereBonus,
                current.safety + safetyBonus,
                current.revenue + revenueBonus
        );
        RATINGS.put(player.getUuid(), updated);
        player.sendMessage(Text.translatable("message.fnaf6.rating.updated", Math.round(updated.overall())), true);
    }

    public static void applyAnimatronicBonus(ServerPlayerEntity player, double entertainmentBonus, double atmosphereBonus,
                                           double safetyBonus, double revenueBonus) {
        updateFromPizzeria(player, entertainmentBonus, atmosphereBonus, safetyBonus, revenueBonus);
    }

    public static long computeDailyRevenue(UUID playerId) {
        FazRating rating = get(playerId);
        long base = Fnaf6Config.get().dailyBaseRevenue;
        double multiplier = 1.0 + (rating.overall() / 100.0);
        return Math.round(base * multiplier);
    }
}
