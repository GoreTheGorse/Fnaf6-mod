package com.fnaf6.mod.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.world.World;

public class ScraptrapEntity extends AbstractAnimatronicEntity {
    public ScraptrapEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return createBaseAttributes(40.0D, 0.31D, 8.0D, 28.0D);
    }
}
