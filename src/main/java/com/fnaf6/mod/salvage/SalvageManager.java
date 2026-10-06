package com.fnaf6.mod.salvage;

import com.fnaf6.mod.Fnaf6Mod;
import com.fnaf6.mod.item.ModItems;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.util.Random;

public final class SalvageManager {
    private SalvageManager() {}

    public static void register() {
        Fnaf6Mod.LOGGER.debug("Registered salvage progression system");
    }

    public static ItemStack rollReward(String animatronicName, double riskLevel) {
        Random random = new Random();
        double normalizedRisk = Math.max(0.0, Math.min(1.0, riskLevel));

        if (normalizedRisk < 0.25) {
            return new ItemStack(ModItems.ANIMATRONIC_PART, 1 + random.nextInt(2));
        }
        if (normalizedRisk < 0.5) {
            return new ItemStack(ModItems.CIRCUIT_BOARD, 1 + random.nextInt(2));
        }
        if (normalizedRisk < 0.8) {
            return new ItemStack(ModItems.BLUEPRINT, 1);
        }

        return new ItemStack(Items.ENDER_EYE, 1 + random.nextInt(2));
    }

    public static double calculateRisk(int dangerLevel, int safetyLevel) {
        double danger = Math.max(0, dangerLevel);
        double safety = Math.max(0, safetyLevel);
        double raw = (danger - safety) / 10.0;
        return Math.max(0.0, Math.min(1.0, raw));
    }
}
