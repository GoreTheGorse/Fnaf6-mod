package com.fnaf6.mod.block;

import com.fnaf6.mod.Fnaf6Mod;
import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class ModBlocks {
    public static final Block SECURITY_DOOR = register("security_door", new SecurityDoorBlock());
    public static final Block CAMERA_MONITOR = register("camera_monitor", new CameraMonitorBlock());

    private ModBlocks() {}

    public static void register() {
        Fnaf6Mod.LOGGER.debug("Registered blocks");
    }

    private static Block register(String name, Block block) {
        return Registry.register(Registries.BLOCK, Identifier.of(Fnaf6Mod.MOD_ID, name), block);
    }
}
