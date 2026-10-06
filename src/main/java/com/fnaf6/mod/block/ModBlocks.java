package com.fnaf6.mod.block;

import com.fnaf6.mod.Fnaf6Mod;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import com.fnaf6.mod.pizzeria.AttractionType;

public class ModBlocks {
    public static final Block CHECKER_FLOOR = register("checker_floor",
            new Block(AbstractBlock.Settings.create()
                    .mapColor(MapColor.WHITE)
                    .strength(1.5f, 6.0f)
                    .requiresTool()
                    .sounds(BlockSoundGroup.STONE)));

    public static final Block PIZZERIA_WALL = register("pizzeria_wall",
            new Block(AbstractBlock.Settings.create()
                    .mapColor(MapColor.PURPLE)
                    .strength(2.0f, 6.0f)
                    .requiresTool()
                    .sounds(BlockSoundGroup.STONE)));

    public static final Block NEON_SIGN = register("neon_sign",
            new Block(AbstractBlock.Settings.create()
                    .mapColor(MapColor.PINK)
                    .strength(0.8f)
                    .luminance(state -> 12)
                    .sounds(BlockSoundGroup.GLASS)
                    .pistonBehavior(PistonBehavior.NORMAL)));

    public static final Block PIZZERIA_TERMINAL = register("pizzeria_terminal",
            new PizzeriaTerminalBlock(AbstractBlock.Settings.create()
                    .mapColor(MapColor.CYAN)
                    .strength(3.0f, 9.0f)
                    .requiresTool()
                    .luminance(state -> 8)
                    .sounds(BlockSoundGroup.METAL)));

    public static final Block PARTY_TABLE = register("party_table",
            new AttractionBlock(AbstractBlock.Settings.create()
                    .mapColor(MapColor.ORANGE)
                    .strength(1.5f)
                    .sounds(BlockSoundGroup.WOOD), AttractionType.PARTY_TABLE));

    public static final Block ARCADE_CABINET = register("arcade_cabinet",
            new AttractionBlock(AbstractBlock.Settings.create()
                    .mapColor(MapColor.BLUE)
                    .strength(2.0f)
                    .luminance(state -> 6)
                    .sounds(BlockSoundGroup.METAL), AttractionType.ARCADE_CABINET));

    public static final Block PRIZE_COUNTER = register("prize_counter",
            new AttractionBlock(AbstractBlock.Settings.create()
                    .mapColor(MapColor.YELLOW)
                    .strength(1.5f)
                    .sounds(BlockSoundGroup.WOOD), AttractionType.PRIZE_COUNTER));

    private static Block register(String name, Block block) {
        Identifier id = Identifier.of(Fnaf6Mod.MOD_ID, name);
        Registry.register(Registries.ITEM, id, new BlockItem(block, new Item.Settings()));
        return Registry.register(Registries.BLOCK, id, block);
    }

    public static void register() {
        Fnaf6Mod.LOGGER.debug("Registered blocks");
    }
}
