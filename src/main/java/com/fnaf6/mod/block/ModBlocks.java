package com.fnaf6.mod.block;

import com.fnaf6.mod.Fnaf6Mod;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class ModBlocks {
    public static final Block SECURITY_DOOR = register("security_door", new SecurityDoorBlock());
    public static final Block CAMERA_MONITOR = register("camera_monitor", new CameraMonitorBlock());
    public static final Block CHECKER_FLOOR = register("checker_floor", new Block(Block.Settings.create().strength(1.5F).mapColor(MapColor.BLACK)));
    public static final Block PIZZERIA_WALL = register("pizzeria_wall", new Block(Block.Settings.create().strength(2.0F).mapColor(MapColor.RED)));
    public static final Block NEON_SIGN = register("neon_sign", new Block(Block.Settings.create().strength(1.0F).mapColor(MapColor.YELLOW)));
    public static final Block PIZZERIA_TERMINAL = register("pizzeria_terminal", new PizzeriaTerminalBlock(Block.Settings.create().strength(2.5F).mapColor(MapColor.CYAN)));
    public static final Block PARTY_TABLE = register("party_table", new Block(Block.Settings.create().strength(1.8F).mapColor(MapColor.ORANGE)));
    public static final Block ARCADE_CABINET = register("arcade_cabinet", new Block(Block.Settings.create().strength(2.0F).mapColor(MapColor.BROWN)));
    public static final Block PRIZE_COUNTER = register("prize_counter", new Block(Block.Settings.create().strength(2.2F).mapColor(MapColor.LIGHT_BLUE)));
    public static final Block SALVAGE_TABLE = register("salvage_table", new Block(Block.Settings.create().strength(2.1F).mapColor(MapColor.GRAY)));
    public static final Block BLUEPRINT_STATION = register("blueprint_station", new Block(Block.Settings.create().strength(2.0F).mapColor(MapColor.LIGHT_GRAY)));
    public static final Block ASSEMBLY_BENCH = register("assembly_bench", new Block(Block.Settings.create().strength(2.4F).mapColor(MapColor.IRON_GRAY)));
    public static final Block RATING_TERMINAL = register("rating_terminal", new PizzeriaTerminalBlock(Block.Settings.create().strength(2.5F).mapColor(MapColor.PURPLE)));

    private ModBlocks() {}

    public static void register() {
        Fnaf6Mod.LOGGER.debug("Registered blocks");
    }

    private static Block register(String name, Block block) {
        return Registry.register(Registries.BLOCK, Identifier.of(Fnaf6Mod.MOD_ID, name), block);
    }
}
