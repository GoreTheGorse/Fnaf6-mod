package com.fnaf6.mod.entity;

import com.fnaf6.mod.Fnaf6Mod;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.HostileEntity;
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

    private ModEntities() {}

    public static void register() {
        FabricDefaultAttributeRegistry.register(LEFTY, LeftyEntity.createAttributes());
        Fnaf6Mod.LOGGER.debug("Registered entities");
    }
}
