package com.fnaf6.mod.command;

import com.fnaf6.mod.rating.FazRating;
import com.fnaf6.mod.rating.RatingManager;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public final class RatingCommands {
    private RatingCommands() {}

    public static LiteralArgumentBuilder<ServerCommandSource> node() {
        return CommandManager.literal("rating")
                .then(CommandManager.literal("info").executes(RatingCommands::info));
    }

    private static int info(CommandContext<ServerCommandSource> ctx) {
        ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();
        FazRating rating = RatingManager.getOrCreate(player.getUuid());
        ctx.getSource().sendFeedback(() -> Text.translatable(
                "message.fnaf6.rating.info",
                Math.round(rating.entertainment),
                Math.round(rating.atmosphere),
                Math.round(rating.safety),
                Math.round(rating.revenue),
                Math.round(rating.overall())
        ), false);
        return 1;
    }
}
