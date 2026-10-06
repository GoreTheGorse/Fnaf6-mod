package com.fnaf6.mod.pizzeria;

import com.fnaf6.mod.block.AttractionBlock;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/** One player-owned pizzeria. Plain data; all mutation happens on the server thread. */
public class Pizzeria {
    public static final int BASE_CAPACITY = 6;
    public static final int CAPACITY_PER_EXPANSION = 4;

    public final UUID id;
    public final UUID owner;
    public String ownerName;
    public String name;
    public String dimension;
    /** Null while the terminal block is missing (the pizzeria is "closed"). */
    @Nullable
    public BlockPos terminal;
    public long bank;
    public long totalEarned;
    public final int[] upgrades = new int[UpgradeType.values().length];
    public final Map<BlockPos, AttractionType> attractions = new LinkedHashMap<>();

    public Pizzeria(UUID id, UUID owner, String ownerName, String name, String dimension) {
        this.id = id;
        this.owner = owner;
        this.ownerName = ownerName;
        this.name = name;
        this.dimension = dimension;
    }

    public boolean isOpen() {
        return terminal != null;
    }

    public int level(UpgradeType type) {
        return upgrades[type.ordinal()];
    }

    public int capacity() {
        return BASE_CAPACITY + CAPACITY_PER_EXPANSION * level(UpgradeType.EXPANSION);
    }

    /** Income for one cycle, with menu/marketing upgrades and the server multiplier applied. */
    public long computeIncome(double serverMultiplier) {
        double sum = 0;
        for (AttractionType type : attractions.values()) {
            sum += type.baseIncome;
        }
        double mult = (1.0 + 0.25 * level(UpgradeType.MENU))
                * (1.0 + 0.15 * level(UpgradeType.MARKETING))
                * serverMultiplier;
        return Math.round(sum * mult);
    }

    /** Drops attractions whose block no longer exists. Only checks loaded chunks. */
    public void pruneAttractions(ServerWorld world) {
        Iterator<Map.Entry<BlockPos, AttractionType>> it = attractions.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<BlockPos, AttractionType> e = it.next();
            BlockPos pos = e.getKey();
            if (!world.getChunkManager().isChunkLoaded(pos.getX() >> 4, pos.getZ() >> 4)) {
                continue;
            }
            BlockState state = world.getBlockState(pos);
            if (!(state.getBlock() instanceof AttractionBlock block) || block.getType() != e.getValue()) {
                it.remove();
            }
        }
    }

    public NbtCompound toNbt() {
        NbtCompound n = new NbtCompound();
        n.putString("id", id.toString());
        n.putString("owner", owner.toString());
        n.putString("ownerName", ownerName);
        n.putString("name", name);
        n.putString("dimension", dimension);
        if (terminal != null) {
            n.putLong("terminal", terminal.asLong());
        }
        n.putLong("bank", bank);
        n.putLong("totalEarned", totalEarned);
        n.putIntArray("upgrades", upgrades.clone());
        NbtList list = new NbtList();
        for (Map.Entry<BlockPos, AttractionType> e : attractions.entrySet()) {
            NbtCompound a = new NbtCompound();
            a.putLong("pos", e.getKey().asLong());
            a.putString("type", e.getValue().id);
            list.add(a);
        }
        n.put("attractions", list);
        return n;
    }

    /** @return the pizzeria, or null if the entry is corrupt. */
    @Nullable
    public static Pizzeria fromNbt(NbtCompound n) {
        try {
            Pizzeria p = new Pizzeria(
                    UUID.fromString(n.getString("id")),
                    UUID.fromString(n.getString("owner")),
                    n.getString("ownerName"),
                    n.getString("name"),
                    n.getString("dimension"));
            if (n.contains("terminal")) {
                p.terminal = BlockPos.fromLong(n.getLong("terminal"));
            }
            p.bank = Math.max(0L, n.getLong("bank"));
            p.totalEarned = Math.max(0L, n.getLong("totalEarned"));
            int[] saved = n.getIntArray("upgrades");
            for (int i = 0; i < Math.min(saved.length, p.upgrades.length); i++) {
                p.upgrades[i] = Math.max(0, Math.min(UpgradeType.MAX_LEVEL, saved[i]));
            }
            NbtList list = n.getList("attractions", NbtElement.COMPOUND_TYPE);
            for (int i = 0; i < list.size(); i++) {
                NbtCompound a = list.getCompound(i);
                AttractionType type = AttractionType.byId(a.getString("type"));
                if (type != null) {
                    p.attractions.put(BlockPos.fromLong(a.getLong("pos")), type);
                }
            }
            return p;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
