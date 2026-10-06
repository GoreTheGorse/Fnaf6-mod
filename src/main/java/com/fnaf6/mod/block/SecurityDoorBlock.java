package com.fnaf6.mod.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SecurityDoorBlock extends Block {
    public SecurityDoorBlock() {
        super(Block.Settings.create().strength(2.5F).materialColor(net.minecraft.block.MapColor.GRAY));
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        super.onStateReplaced(state, world, pos, newState, moved);
    }
}
