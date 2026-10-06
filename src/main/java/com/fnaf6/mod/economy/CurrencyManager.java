package com.fnaf6.mod.economy;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Server-side Faz-Coin balances. Saved with the world, so nothing important lives on the client.
 * Stage 2 will move balances from per-player to per-pizzeria accounts.
 */
public class CurrencyManager extends PersistentState {
    private static final String KEY = "fnaf6_currency";

    private static final PersistentState.Type<CurrencyManager> TYPE =
            new PersistentState.Type<>(CurrencyManager::new, CurrencyManager::fromNbt, null);

    private final Map<UUID, Long> balances = new HashMap<>();

    public static CurrencyManager get(MinecraftServer server) {
        PersistentStateManager manager = server.getOverworld().getPersistentStateManager();
        return manager.getOrCreate(TYPE, KEY);
    }

    public long getBalance(UUID player) {
        return balances.getOrDefault(player, 0L);
    }

    public void setBalance(UUID player, long amount) {
        balances.put(player, Math.max(0L, amount));
        markDirty();
    }

    public void add(UUID player, long amount) {
        setBalance(player, getBalance(player) + amount);
    }

    /** @return true if the player had enough and the amount was deducted. */
    public boolean spend(UUID player, long amount) {
        long current = getBalance(player);
        if (amount < 0 || current < amount) {
            return false;
        }
        setBalance(player, current - amount);
        return true;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        NbtCompound list = new NbtCompound();
        for (Map.Entry<UUID, Long> entry : balances.entrySet()) {
            list.putLong(entry.getKey().toString(), entry.getValue());
        }
        nbt.put("balances", list);
        return nbt;
    }

    public static CurrencyManager fromNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        CurrencyManager manager = new CurrencyManager();
        NbtCompound list = nbt.getCompound("balances");
        for (String key : list.getKeys()) {
            try {
                manager.balances.put(UUID.fromString(key), list.getLong(key));
            } catch (IllegalArgumentException ignored) {
                // Skip corrupt entries rather than crash the world load.
            }
        }
        return manager;
    }
}
