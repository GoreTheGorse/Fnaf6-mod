package com.fnaf6.mod;

import com.fnaf6.mod.block.ModBlocks;
import com.fnaf6.mod.command.FazCommands;
import com.fnaf6.mod.config.Fnaf6Config;
import com.fnaf6.mod.entity.ModEntities;
import com.fnaf6.mod.item.ModItemGroups;
import com.fnaf6.mod.item.ModItems;
import com.fnaf6.mod.pizzeria.PizzeriaEvents;
import com.fnaf6.mod.rating.RatingManager;
import com.fnaf6.mod.salvage.SalvageManager;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Fnaf6Mod implements ModInitializer {
    public static final String MOD_ID = "fnaf6";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        Fnaf6Config.load();
        ModBlocks.register();
        ModItems.register();
        ModItemGroups.register();
        ModEntities.register();
        SalvageManager.register();
        RatingManager.register();
        FazCommands.register();
        PizzeriaEvents.register();
        LOGGER.info("Fazbear Entertainment systems online.");
    }
}
