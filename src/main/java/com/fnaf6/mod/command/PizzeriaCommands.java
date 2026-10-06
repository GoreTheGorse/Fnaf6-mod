package com.fnaf6.mod.command;

import com.fnaf6.mod.economy.CurrencyManager;
import com.fnaf6.mod.pizzeria.Pizzeria;
import com.fnaf6.mod.pizzeria.PizzeriaManager;
import com.fnaf6.mod.pizzeria.PizzeriaMessages;
import com.fnaf6.mod.pizzeria.UpgradeType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

/** /fnaf6 pizzeria ... : info, rename, upgrade, withdraw, deposit. Always acts on the caller's own pizzeria. */
public final class PizzeriaCommands {
    private PizzeriaCommands() {}

    public static LiteralArgumentBuilder<ServerCommandSource> node() {
        LiteralArgumentBuilder<ServerCommandSource> root = CommandManager.literal("pizzeria");

        root.then(CommandManager.literal("info").executes(ctx -> info(ctx)));

        root.then(CommandManager.literal("rename")
                .then(CommandManager.argument("name", StringArgumentType.greedyString())
                        .executes(ctx -> rename(ctx, StringArgumentType.getString(ctx, "name")))));

        LiteralArgumentBuilder<ServerCommandSource> upgrade = CommandManager.literal("upgrade");
        for (UpgradeType type : UpgradeType.values()) {
            upgrade.then(CommandManager.literal(type.id).executes(ctx -> upgrade(ctx, type)));
        }
        root.then(upgrade);

        root.then(CommandManager.literal("withdraw")
                .then(CommandManager.literal("all").executes(ctx -> withdraw(ctx, -1)))
                .then(CommandManager.argument("amount", LongArgumentType.longArg(1))
                        .executes(ctx -> withdraw(ctx, LongArgumentType.getLong(ctx, "amount")))));

        root.then(CommandManager.literal("deposit")
                .then(CommandManager.argument("amount", LongArgumentType.longArg(1))
                        .executes(ctx -> deposit(ctx, LongArgumentType.getLong(ctx, "amount")))));

        return root;
    }

    /** Resolves the caller's pizzeria or tells them they have none. Returns null on failure. */
    private static Pizzeria resolve(CommandContext<ServerCommandSource> ctx, ServerPlayerEntity player) {
        Pizzeria p = PizzeriaManager.get(ctx.getSource().getServer()).resolveFor(player);
        if (p == null) {
            ctx.getSource().sendError(Text.translatable("message.fnaf6.pizzeria.none"));
        }
        return p;
    }

    private static int info(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();
        Pizzeria p = resolve(ctx, player);
        if (p == null) {
            return 0;
        }
        PizzeriaMessages.sendSummary(player, p);
        return 1;
    }

    private static int rename(CommandContext<ServerCommandSource> ctx, String raw) throws CommandSyntaxException {
        ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();
        Pizzeria p = resolve(ctx, player);
        if (p == null) {
            return 0;
        }
        String name = raw.trim();
        if (name.length() > 32) {
            name = name.substring(0, 32);
        }
        if (name.isEmpty()) {
            ctx.getSource().sendError(Text.translatable("message.fnaf6.pizzeria.bad_name"));
            return 0;
        }
        p.name = name;
        PizzeriaManager.get(ctx.getSource().getServer()).markDirty();
        String finalName = name;
        ctx.getSource().sendFeedback(() -> Text.translatable("message.fnaf6.pizzeria.renamed", finalName), false);
        return 1;
    }

    private static int upgrade(CommandContext<ServerCommandSource> ctx, UpgradeType type) throws CommandSyntaxException {
        ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();
        Pizzeria p = resolve(ctx, player);
        if (p == null) {
            return 0;
        }
        int level = p.level(type);
        if (level >= UpgradeType.MAX_LEVEL) {
            ctx.getSource().sendError(Text.translatable("message.fnaf6.pizzeria.maxed"));
            return 0;
        }
        long cost = type.costFor(level);
        if (p.bank < cost) {
            ctx.getSource().sendError(Text.translatable("message.fnaf6.pizzeria.upgrade_poor", cost, p.bank));
            return 0;
        }
        p.bank -= cost;
        p.upgrades[type.ordinal()] = level + 1;
        PizzeriaManager.get(ctx.getSource().getServer()).markDirty();
        int newLevel = level + 1;
        ctx.getSource().sendFeedback(() -> Text.translatable("message.fnaf6.pizzeria.upgraded",
                Text.translatable(type.translationKey()), newLevel, UpgradeType.MAX_LEVEL, cost), false);
        return 1;
    }

    private static int withdraw(CommandContext<ServerCommandSource> ctx, long requested) throws CommandSyntaxException {
        ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();
        Pizzeria p = resolve(ctx, player);
        if (p == null) {
            return 0;
        }
        long amount = requested < 0 ? p.bank : requested;
        if (amount <= 0 || amount > p.bank) {
            ctx.getSource().sendError(Text.translatable("message.fnaf6.pizzeria.not_enough_bank", p.bank));
            return 0;
        }
        p.bank -= amount;
        CurrencyManager.get(ctx.getSource().getServer()).add(player.getUuid(), amount);
        PizzeriaManager.get(ctx.getSource().getServer()).markDirty();
        ctx.getSource().sendFeedback(() -> Text.translatable("message.fnaf6.pizzeria.withdrew", amount), false);
        return 1;
    }

    private static int deposit(CommandContext<ServerCommandSource> ctx, long amount) throws CommandSyntaxException {
        ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();
        Pizzeria p = resolve(ctx, player);
        if (p == null) {
            return 0;
        }
        if (!CurrencyManager.get(ctx.getSource().getServer()).spend(player.getUuid(), amount)) {
            ctx.getSource().sendError(Text.translatable("message.fnaf6.pizzeria.not_enough_wallet"));
            return 0;
        }
        p.bank += amount;
        PizzeriaManager.get(ctx.getSource().getServer()).markDirty();
        ctx.getSource().sendFeedback(() -> Text.translatable("message.fnaf6.pizzeria.deposited", amount), false);
        return 1;
    }
}
