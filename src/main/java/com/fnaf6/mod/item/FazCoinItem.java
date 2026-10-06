package com.fnaf6.mod.item;

import com.fnaf6.mod.economy.CurrencyManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

/** Physical coin. Right-click deposits one into your balance; sneak + right-click deposits the whole stack. */
public class FazCoinItem extends Item {
    public FazCoinItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (!world.isClient && user instanceof ServerPlayerEntity player) {
            int amount = user.isSneaking() ? stack.getCount() : 1;
            CurrencyManager currency = CurrencyManager.get(player.getServer());
            currency.add(player.getUuid(), amount);
            stack.decrement(amount);
            player.sendMessage(Text.translatable("message.fnaf6.deposited", amount,
                    currency.getBalance(player.getUuid())), true);
        }
        return TypedActionResult.success(stack, world.isClient());
    }
}
