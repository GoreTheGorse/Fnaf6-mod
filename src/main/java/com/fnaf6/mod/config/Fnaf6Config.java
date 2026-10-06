package com.fnaf6.mod.config;

import com.fnaf6.mod.Fnaf6Mod;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Server-owner configuration, stored at config/fnaf6.json.
 * Unknown or missing keys fall back to defaults, and the file is rewritten on load
 * so new options appear automatically after updates.
 */
public class Fnaf6Config {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static Fnaf6Config instance = new Fnaf6Config();

    // --- Gameplay ---
    public double animatronicDifficulty = 1.0;
    public double spawnRateMultiplier = 1.0;
    public int nightLengthSeconds = 360;
    public double moneyRewardMultiplier = 1.0;
    public double salvageDifficulty = 1.0;
    public float jumpscareDamage = 20.0f;
    public boolean animatronicsCanAttackOutsideOffice = false;
    public boolean finalFireEnabled = true;
    public boolean pizzeriaManagementEnabled = true;
    public double riskMultiplier = 1.0;

    // --- Pizzeria management (Stage 2) ---
    public int pizzeriaIncomeIntervalSeconds = 60;
    public int pizzeriaRadius = 24;
    public int maxPizzeriasPerPlayer = 1;
    public long pizzeriaFoundingCost = 100;

    // --- Rating system ---
    public int ratingMax = 100;
    public int ratingUpdateIntervalTicks = 200;
    public long dailyBaseRevenue = 50;
    public boolean enableRatingSystem = true;

    // --- Audio ---
    public double soundVolume = 1.0;

    public static Fnaf6Config get() {
        return instance;
    }

    private static Path path() {
        return FabricLoader.getInstance().getConfigDir().resolve("fnaf6.json");
    }

    public static void load() {
        Path file = path();
        if (Files.exists(file)) {
            try (Reader reader = Files.newBufferedReader(file)) {
                Fnaf6Config loaded = GSON.fromJson(reader, Fnaf6Config.class);
                if (loaded != null) {
                    instance = loaded;
                }
            } catch (Exception e) {
                Fnaf6Mod.LOGGER.error("Could not read fnaf6.json, using defaults", e);
            }
        }
        instance.sanitize();
        save();
    }

    public static void save() {
        try (Writer writer = Files.newBufferedWriter(path())) {
            GSON.toJson(instance, writer);
        } catch (IOException e) {
            Fnaf6Mod.LOGGER.error("Could not write fnaf6.json", e);
        }
    }

    /** Clamp values so a typo in the file can't break the game. */
    private void sanitize() {
        animatronicDifficulty = clamp(animatronicDifficulty, 0.0, 5.0);
        spawnRateMultiplier = clamp(spawnRateMultiplier, 0.0, 10.0);
        nightLengthSeconds = (int) clamp(nightLengthSeconds, 60, 3600);
        moneyRewardMultiplier = clamp(moneyRewardMultiplier, 0.0, 100.0);
        salvageDifficulty = clamp(salvageDifficulty, 0.1, 5.0);
        jumpscareDamage = (float) clamp(jumpscareDamage, 0.0, 1000.0);
        soundVolume = clamp(soundVolume, 0.0, 2.0);
        riskMultiplier = clamp(riskMultiplier, 0.0, 10.0);
        pizzeriaIncomeIntervalSeconds = (int) clamp(pizzeriaIncomeIntervalSeconds, 10, 3600);
        pizzeriaRadius = (int) clamp(pizzeriaRadius, 8, 64);
        maxPizzeriasPerPlayer = (int) clamp(maxPizzeriasPerPlayer, 1, 10);
        pizzeriaFoundingCost = (long) clamp(pizzeriaFoundingCost, 0, 1_000_000);
        ratingMax = (int) clamp(ratingMax, 1, 100);
        ratingUpdateIntervalTicks = (int) clamp(ratingUpdateIntervalTicks, 20, 1200);
        dailyBaseRevenue = Math.max(0L, dailyBaseRevenue);
        enableRatingSystem = enableRatingSystem;
    }

    private static double clamp(double v, double min, double max) {
        return Math.max(min, Math.min(max, v));
    }
}
