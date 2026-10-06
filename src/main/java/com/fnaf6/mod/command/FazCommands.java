package com.fnaf6.mod.command;

import com.fnaf6.mod.economy.CurrencyManager;
import com.mojang.brigadier.arguments.LongArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class FazCommands {
    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                dispatcher.register(CommandManager.literal("fnaf6")
                        .then(CommandManager.literal("balance")
                                .executes(ctx -> {
                                    ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();
                                    long bal = CurrencyManager.get(ctx.getSource().getServer()).getBalance(player.getUuid());
                                    ctx.getSource().sendFeedback(() -> Text.translatable("message.fnaf6.balance", bal), false);
                                    return 1;
                                }))
                        .then(PizzeriaCommands.node())
                        .then(CommandManager.literal("give")
                                .requires(src -> src.hasPermissionLevel(2))
                                .then(CommandManager.argument("target", EntityArgumentType.player())
                                        .then(CommandManager.argument("amount", LongArgumentType.longArg(1))
                                                .executes(ctx -> {
                                                    ServerPlayerEntity target = EntityArgumentType.getPlayer(ctx, "target");
                                                    long amount = LongArgumentType.getLong(ctx, "amount");
                                                    CurrencyManager.get(ctx.getSource().getServer()).add(target.getUuid(), amount);
                                                    ctx.getSource().sendFeedback(() -> Text.translatable(
                                                            "message.fnaf6.gave", amount, target.getDisplayName()), true);
                                                    return 1;
                                                }))))));
    }
}
