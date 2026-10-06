package com.fnaf6.mod.entity;

import com.fnaf6.mod.Fnaf6Mod;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;

public abstract class AbstractAnimatronicEntity extends HostileEntity {
    private AnimatronicState state = AnimatronicState.IDLE;
    private int malfunctionTicks;

    protected AbstractAnimatronicEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(1, new MeleeAttackGoal(this, 1.1D, false));
        this.goalSelector.add(2, new WanderAroundGoal(this, 1.0D));
        this.goalSelector.add(3, new LookAroundGoal(this));
        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getWorld().isClient) {
            return;
        }

        if (this.isAlive()) {
            if (this.getTarget() != null) {
                setState(AnimatronicState.PATROL);
                malfunctionTicks = 0;
            } else if (this.random.nextInt(200) == 0) {
                setState(AnimatronicState.IDLE);
            }

            if (this.age % 200 == 0 && this.random.nextInt(10) == 0) {
                setState(AnimatronicState.MALFUNCTION);
                malfunctionTicks = 40;
            }

            if (malfunctionTicks > 0) {
                malfunctionTicks--;
                if (malfunctionTicks == 0) {
                    setState(AnimatronicState.SHUTDOWN);
                }
            }
        }
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putString("AnimatronicState", state.name());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        String savedState = nbt.getString("AnimatronicState");
        try {
            state = AnimatronicState.valueOf(savedState);
        } catch (IllegalArgumentException ignored) {
            state = AnimatronicState.IDLE;
        }
    }

    public AnimatronicState getState() {
        return state;
    }

    public void setState(AnimatronicState newState) {
        if (newState != null) {
            this.state = newState;
        }
    }

    public static DefaultAttributeContainer.Builder createBaseAttributes(double maxHealth, double speed, double attackDamage, double followRange) {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, maxHealth)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, speed)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, attackDamage)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, followRange);
    }

    protected void triggerJumpscare() {
        setState(AnimatronicState.JUMPSCARE);
        if (!this.getWorld().isClient) {
            Fnaf6Mod.LOGGER.debug("Animatronic jumpscare triggered at {}", this.getBlockPos());
        }
    }
}
