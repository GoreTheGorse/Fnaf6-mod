package com.fnaf6.mod.item;

import com.fnaf6.mod.economy.CurrencyManager;
import com.fnaf6.mod.pizzeria.Pizzeria;
import com.fnaf6.mod.pizzeria.PizzeriaManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

/**
 * Shows your wallet and a one-line summary of each pizzeria you own. A later stage turns this into the management GUI opener.
 */
public class FazTabletItem extends Item {
    public FazTabletItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (!world.isClient && user instanceof ServerPlayerEntity player) {
            long balance = CurrencyManager.get(player.getServer()).getBalance(player.getUuid());
            player.sendMessage(Text.translatable("message.fnaf6.balance", balance), false);
            for (Pizzeria p : PizzeriaManager.get(player.getServer()).ownedBy(player.getUuid())) {
                player.sendMessage(Text.translatable("message.fnaf6.tablet_pizzeria",
                        p.name, p.bank, p.attractions.size(), p.capacity()), false);
            }
        }
        return TypedActionResult.success(stack, world.isClient());
    }
}
