package com.fnaf6.mod.pizzeria;

import com.fnaf6.mod.block.AttractionBlock;
import com.fnaf6.mod.block.PizzeriaTerminalBlock;
import com.fnaf6.mod.config.Fnaf6Config;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

/** Server tick (income) and block-break protection for pizzeria blocks. */
public final class PizzeriaEvents {
    private static int ticks;

    private PizzeriaEvents() {}

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            int interval = Math.max(1, Fnaf6Config.get().pizzeriaIncomeIntervalSeconds) * 20;
            if (++ticks >= interval) {
                ticks = 0;
                PizzeriaManager.get(server).runIncome(server);
            }
        });

        // Only the owner (or an operator) may break a registered terminal or attraction.
        PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, blockEntity) -> {
            if (world.isClient || !(player instanceof ServerPlayerEntity sp)) {
                return true;
            }
            boolean relevant = state.getBlock() instanceof PizzeriaTerminalBlock
                    || state.getBlock() instanceof AttractionBlock;
            if (!relevant) {
                return true;
            }
            PizzeriaManager manager = PizzeriaManager.get(sp.getServer());
            String dim = PizzeriaManager.dimId(world);
            Pizzeria p = manager.findByTerminal(dim, pos);
            if (p == null) {
                p = manager.findByAttraction(dim, pos);
            }
            if (p == null || p.owner.equals(sp.getUuid())
                    || sp.getServer().getPlayerManager().isOperator(sp.getGameProfile())) {
                return true;
            }
            sp.sendMessage(Text.translatable("message.fnaf6.pizzeria.protected", p.ownerName), true);
            return false;
        });
    }
}
