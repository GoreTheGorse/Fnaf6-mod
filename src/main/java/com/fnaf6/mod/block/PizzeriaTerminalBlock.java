package com.fnaf6.mod.block;

import com.fnaf6.mod.config.Fnaf6Config;
import com.fnaf6.mod.economy.CurrencyManager;
import com.fnaf6.mod.pizzeria.Pizzeria;
import com.fnaf6.mod.pizzeria.PizzeriaManager;
import com.fnaf6.mod.pizzeria.PizzeriaMessages;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Placing this founds (or reopens) your pizzeria. Right-click for status,
 * sneak + right-click as the owner to collect the bank into your wallet.
 */
public class PizzeriaTerminalBlock extends Block {
    public PizzeriaTerminalBlock(Settings settings) {
        super(settings);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        if (world.isClient || !(world instanceof ServerWorld serverWorld)
                || !(placer instanceof ServerPlayerEntity player)) {
            return;
        }
        PizzeriaManager manager = PizzeriaManager.get(serverWorld.getServer());
        PizzeriaManager.OpenResult result = manager.openTerminal(player, serverWorld, pos);
        Fnaf6Config cfg = Fnaf6Config.get();
        switch (result) {
            case CREATED -> player.sendMessage(Text.translatable("message.fnaf6.pizzeria.created",
                    cfg.pizzeriaFoundingCost, cfg.pizzeriaRadius), false);
            case REOPENED -> player.sendMessage(Text.translatable("message.fnaf6.pizzeria.reopened"), false);
            case DISABLED -> reject(serverWorld, pos, player, Text.translatable("message.fnaf6.pizzeria.disabled"));
            case LIMIT -> reject(serverWorld, pos, player,
                    Text.translatable("message.fnaf6.pizzeria.limit", cfg.maxPizzeriasPerPlayer));
            case CANNOT_AFFORD -> reject(serverWorld, pos, player,
                    Text.translatable("message.fnaf6.pizzeria.cannot_afford", cfg.pizzeriaFoundingCost));
        }
    }

    private static void reject(ServerWorld world, BlockPos pos, ServerPlayerEntity player, Text reason) {
        world.breakBlock(pos, true);
        player.sendMessage(reason, false);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world.isClient || !(player instanceof ServerPlayerEntity sp)) {
            return ActionResult.SUCCESS;
        }
        PizzeriaManager manager = PizzeriaManager.get(sp.getServer());
        Pizzeria p = manager.findByTerminal(PizzeriaManager.dimId(world), pos);
        if (p == null) {
            sp.sendMessage(Text.translatable("message.fnaf6.pizzeria.unregistered"), true);
            return ActionResult.SUCCESS;
        }
        if (sp.isSneaking() && p.owner.equals(sp.getUuid())) {
            long amount = p.bank;
            if (amount > 0) {
                CurrencyManager.get(sp.getServer()).add(sp.getUuid(), amount);
                p.bank = 0;
                manager.markDirty();
            }
            sp.sendMessage(Text.translatable("message.fnaf6.pizzeria.collected", amount), true);
        } else {
            PizzeriaMessages.sendSummary(sp, p);
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!world.isClient && !state.isOf(newState.getBlock())) {
            PizzeriaManager.get(world.getServer()).closeByTerminal(PizzeriaManager.dimId(world), pos);
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }
}
