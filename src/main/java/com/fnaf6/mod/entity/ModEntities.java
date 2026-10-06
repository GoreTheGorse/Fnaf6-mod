package com.fnaf6.mod.entity;

import com.fnaf6.mod.Fnaf6Mod;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class ModEntities {
    public static final EntityType<LeftyEntity> LEFTY = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(Fnaf6Mod.MOD_ID, "lefty"),
            EntityType.Builder.create(LeftyEntity::new, SpawnGroup.MONSTER)
                    .dimensions(0.7F, 1.8F)
                    .build()
    );

    public static final EntityType<ScraptrapEntity> SCRAPTRAP = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(Fnaf6Mod.MOD_ID, "scraptrap"),
            EntityType.Builder.create(ScraptrapEntity::new, SpawnGroup.MONSTER)
                    .dimensions(0.8F, 1.9F)
                    .build()
    );

    private ModEntities() {}

    public static void register() {
        FabricDefaultAttributeRegistry.register(LEFTY, LeftyEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(SCRAPTRAP, ScraptrapEntity.createAttributes());
        Fnaf6Mod.LOGGER.debug("Registered animatronic entities");
    }
}
