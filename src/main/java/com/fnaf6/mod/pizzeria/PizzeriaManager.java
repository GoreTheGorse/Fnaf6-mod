package com.fnaf6.mod.pizzeria;

import com.fnaf6.mod.block.ModBlocks;
import com.fnaf6.mod.config.Fnaf6Config;
import com.fnaf6.mod.economy.CurrencyManager;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/** All pizzerias in the world, saved with the world. The server is the only authority. */
public class PizzeriaManager extends PersistentState {
    private static final String KEY = "fnaf6_pizzerias";

    private static final PersistentState.Type<PizzeriaManager> TYPE =
            new PersistentState.Type<>(PizzeriaManager::new, PizzeriaManager::fromNbt, null);

    public enum OpenResult { CREATED, REOPENED, DISABLED, LIMIT, CANNOT_AFFORD }

    private final Map<UUID, Pizzeria> pizzerias = new LinkedHashMap<>();

    public static PizzeriaManager get(MinecraftServer server) {
        PersistentStateManager manager = server.getOverworld().getPersistentStateManager();
        return manager.getOrCreate(TYPE, KEY);
    }

    public static String dimId(World world) {
        return world.getRegistryKey().getValue().toString();
    }

    // ---------- lookups ----------

    @Nullable
    public Pizzeria findByTerminal(String dimension, BlockPos pos) {
        for (Pizzeria p : pizzerias.values()) {
            if (p.terminal != null && p.terminal.equals(pos) && p.dimension.equals(dimension)) {
                return p;
            }
        }
        return null;
    }

    @Nullable
    public Pizzeria findByAttraction(String dimension, BlockPos pos) {
        for (Pizzeria p : pizzerias.values()) {
            if (p.dimension.equals(dimension) && p.attractions.containsKey(pos)) {
                return p;
            }
        }
        return null;
    }

    /** An open pizzeria owned by this player whose radius contains pos. */
    @Nullable
    public Pizzeria findInRange(String dimension, BlockPos pos, UUID owner) {
        double r = Fnaf6Config.get().pizzeriaRadius;
        for (Pizzeria p : pizzerias.values()) {
            if (p.terminal != null && p.owner.equals(owner) && p.dimension.equals(dimension)
                    && p.terminal.getSquaredDistance(pos) <= r * r) {
                return p;
            }
        }
        return null;
    }

    public List<Pizzeria> ownedBy(UUID owner) {
        List<Pizzeria> result = new ArrayList<>();
        for (Pizzeria p : pizzerias.values()) {
            if (p.owner.equals(owner)) {
                result.add(p);
            }
        }
        return result;
    }

    private int openCount(UUID owner) {
        int n = 0;
        for (Pizzeria p : ownedBy(owner)) {
            if (p.isOpen()) {
                n++;
            }
        }
        return n;
    }

    /** The pizzeria a command should act on: nearest open one in the player's dimension, else any they own. */
    @Nullable
    public Pizzeria resolveFor(ServerPlayerEntity player) {
        String dim = dimId(player.getWorld());
        Pizzeria best = null;
        double bestDist = Double.MAX_VALUE;
        Pizzeria fallback = null;
        for (Pizzeria p : ownedBy(player.getUuid())) {
            if (fallback == null) {
                fallback = p;
            }
            if (p.terminal != null && p.dimension.equals(dim)) {
                double d = p.terminal.getSquaredDistance(player.getBlockPos());
                if (d < bestDist) {
                    bestDist = d;
                    best = p;
                }
            }
        }
        return best != null ? best : fallback;
    }

    // ---------- terminal lifecycle ----------

    public OpenResult openTerminal(ServerPlayerEntity player, ServerWorld world, BlockPos pos) {
        Fnaf6Config cfg = Fnaf6Config.get();
        if (!cfg.pizzeriaManagementEnabled) {
            return OpenResult.DISABLED;
        }
        UUID owner = player.getUuid();
        if (openCount(owner) >= cfg.maxPizzeriasPerPlayer) {
            return OpenResult.LIMIT;
        }
        BlockPos immutable = pos.toImmutable();
        String dim = dimId(world);

        for (Pizzeria p : ownedBy(owner)) {
            if (!p.isOpen()) {
                p.terminal = immutable;
                p.dimension = dim;
                p.ownerName = player.getGameProfile().getName();
                // Keep only attractions that are still inside the radius of the new terminal.
                double r = cfg.pizzeriaRadius;
                p.attractions.keySet().removeIf(a -> a.getSquaredDistance(immutable) > r * r);
                markDirty();
                return OpenResult.REOPENED;
            }
        }

        if (!CurrencyManager.get(world.getServer()).spend(owner, cfg.pizzeriaFoundingCost)) {
            return OpenResult.CANNOT_AFFORD;
        }
        String ownerName = player.getGameProfile().getName();
        Pizzeria p = new Pizzeria(UUID.randomUUID(), owner, ownerName, ownerName + "'s Pizzeria", dim);
        p.terminal = immutable;
        pizzerias.put(p.id, p);
        markDirty();
        return OpenResult.CREATED;
    }

    /** Called when a terminal block leaves the world. The pizzeria is kept, just closed. */
    public void closeByTerminal(String dimension, BlockPos pos) {
        Pizzeria p = findByTerminal(dimension, pos);
        if (p != null) {
            p.terminal = null;
            markDirty();
        }
    }

    public void removeAttraction(String dimension, BlockPos pos) {
        Pizzeria p = findByAttraction(dimension, pos);
        if (p != null && p.attractions.remove(pos) != null) {
            markDirty();
        }
    }

    // ---------- income ----------

    /** Runs one income cycle for every pizzeria whose terminal chunk is loaded. */
    public void runIncome(MinecraftServer server) {
        Fnaf6Config cfg = Fnaf6Config.get();
        if (!cfg.pizzeriaManagementEnabled) {
            return;
        }
        boolean changed = false;
        for (Pizzeria p : pizzerias.values()) {
            if (p.terminal == null) {
                continue;
            }
            ServerWorld world = worldFor(server, p.dimension);
            if (world == null
                    || !world.getChunkManager().isChunkLoaded(p.terminal.getX() >> 4, p.terminal.getZ() >> 4)) {
                continue;
            }
            // Safety net for /setblock, WorldEdit and similar: no terminal block means closed.
            if (!world.getBlockState(p.terminal).isOf(ModBlocks.PIZZERIA_TERMINAL)) {
                p.terminal = null;
                changed = true;
                continue;
            }
            int before = p.attractions.size();
            p.pruneAttractions(world);
            changed |= before != p.attractions.size();

            long earned = p.computeIncome(cfg.moneyRewardMultiplier);
            if (earned > 0) {
                p.bank += earned;
                p.totalEarned += earned;
                changed = true;
            }
        }
        if (changed) {
            markDirty();
        }
    }

    @Nullable
    private static ServerWorld worldFor(MinecraftServer server, String dimension) {
        Identifier id = Identifier.tryParse(dimension);
        return id == null ? null : server.getWorld(RegistryKey.of(RegistryKeys.WORLD, id));
    }

    // ---------- persistence ----------

    @Override
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        NbtList list = new NbtList();
        for (Pizzeria p : pizzerias.values()) {
            list.add(p.toNbt());
        }
        nbt.put("pizzerias", list);
        return nbt;
    }

    public static PizzeriaManager fromNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        PizzeriaManager manager = new PizzeriaManager();
        NbtList list = nbt.getList("pizzerias", NbtElement.COMPOUND_TYPE);
        for (int i = 0; i < list.size(); i++) {
            Pizzeria p = Pizzeria.fromNbt(list.getCompound(i));
            if (p != null) {
                manager.pizzerias.put(p.id, p);
            }
        }
        return manager;
    }
}
