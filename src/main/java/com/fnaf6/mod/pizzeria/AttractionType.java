package com.fnaf6.mod.pizzeria;

import org.jetbrains.annotations.Nullable;

/** Blocks that earn money for a pizzeria while they stand inside its radius. */
public enum AttractionType {
    PARTY_TABLE("party_table", 4),
    ARCADE_CABINET("arcade_cabinet", 7),
    PRIZE_COUNTER("prize_counter", 10);

    public final String id;
    /** Faz-Coins earned per income cycle before multipliers. */
    public final int baseIncome;

    AttractionType(String id, int baseIncome) {
        this.id = id;
        this.baseIncome = baseIncome;
    }

    @Nullable
    public static AttractionType byId(String id) {
        for (AttractionType t : values()) {
            if (t.id.equals(id)) {
                return t;
            }
        }
        return null;
    }
}
