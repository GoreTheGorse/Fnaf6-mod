package com.fnaf6.mod.item;

import com.fnaf6.mod.Fnaf6Mod;
import com.fnaf6.mod.block.ModBlocks;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroups {
    public static final ItemGroup MAIN = Registry.register(
            Registries.ITEM_GROUP,
            Identifier.of(Fnaf6Mod.MOD_ID, "main"),
            FabricItemGroup.builder()
                    .icon(() -> new ItemStack(ModItems.FAZ_COIN))
                    .displayName(Text.translatable("itemGroup.fnaf6.main"))
                    .entries((context, entries) -> {
                        entries.add(ModItems.FAZ_COIN);
                        entries.add(ModItems.FAZ_TABLET);
                        entries.add(ModBlocks.CHECKER_FLOOR);
                        entries.add(ModBlocks.PIZZERIA_WALL);
                        entries.add(ModBlocks.NEON_SIGN);
                        entries.add(ModBlocks.PIZZERIA_TERMINAL);
                        entries.add(ModBlocks.PARTY_TABLE);
                        entries.add(ModBlocks.ARCADE_CABINET);
                        entries.add(ModBlocks.PRIZE_COUNTER);
                    })
                    .build());

    public static void register() {
        Fnaf6Mod.LOGGER.debug("Registered item groups");
    }
}
