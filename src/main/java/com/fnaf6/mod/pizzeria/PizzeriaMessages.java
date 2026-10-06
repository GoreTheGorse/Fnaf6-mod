package com.fnaf6.mod.pizzeria;

import com.fnaf6.mod.config.Fnaf6Config;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

/** Chat output for pizzeria status. A GUI replaces this in a later stage. */
public final class PizzeriaMessages {
    private PizzeriaMessages() {}

    public static void sendSummary(ServerPlayerEntity viewer, Pizzeria p) {
        viewer.sendMessage(Text.translatable("message.fnaf6.pizzeria.header", p.name, p.ownerName), false);
        if (!p.isOpen()) {
            viewer.sendMessage(Text.translatable("message.fnaf6.pizzeria.closed"), false);
        }
        viewer.sendMessage(Text.translatable("message.fnaf6.pizzeria.bank", p.bank, p.totalEarned), false);
        viewer.sendMessage(Text.translatable("message.fnaf6.pizzeria.income",
                p.computeIncome(Fnaf6Config.get().moneyRewardMultiplier),
                Fnaf6Config.get().pizzeriaIncomeIntervalSeconds), false);
        viewer.sendMessage(Text.translatable("message.fnaf6.pizzeria.attractions",
                p.attractions.size(), p.capacity()), false);

        StringBuilder sb = new StringBuilder();
        for (UpgradeType t : UpgradeType.values()) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(Text.translatable(t.translationKey()).getString())
                    .append(' ').append(p.level(t)).append('/').append(UpgradeType.MAX_LEVEL);
        }
        viewer.sendMessage(Text.translatable("message.fnaf6.pizzeria.upgrades", sb.toString()), false);
    }
}
