package com.fnaf6.mod.block;

import com.fnaf6.mod.pizzeria.AttractionType;
import com.fnaf6.mod.pizzeria.Pizzeria;
import com.fnaf6.mod.pizzeria.PizzeriaManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/** Earns Faz-Coins for the owner's pizzeria when placed inside its radius, up to its capacity. */
public class AttractionBlock extends Block {
    private final AttractionType type;

    public AttractionBlock(Settings settings, AttractionType type) {
        super(settings);
        this.type = type;
    }

    public AttractionType getType() {
        return type;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        if (world.isClient || !(placer instanceof ServerPlayerEntity player)) {
            return;
        }
        PizzeriaManager manager = PizzeriaManager.get(player.getServer());
        Pizzeria p = manager.findInRange(PizzeriaManager.dimId(world), pos, player.getUuid());
        if (p == null) {
            player.sendMessage(Text.translatable("message.fnaf6.attraction.unconnected"), true);
        } else if (p.attractions.size() >= p.capacity()) {
            player.sendMessage(Text.translatable("message.fnaf6.attraction.full", p.capacity()), true);
        } else {
            p.attractions.put(pos.toImmutable(), type);
            manager.markDirty();
            player.sendMessage(Text.translatable("message.fnaf6.attraction.connected",
                    p.attractions.size(), p.capacity()), true);
        }
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!world.isClient && !state.isOf(newState.getBlock())) {
            PizzeriaManager.get(world.getServer()).removeAttraction(PizzeriaManager.dimId(world), pos);
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }
}
