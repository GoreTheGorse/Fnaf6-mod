package com.fnaf6.mod.pizzeria;

/** Pizzeria upgrades, bought with the pizzeria's bank balance. */
public enum UpgradeType {
    MENU("menu", 250),        // +25% income per level
    MARKETING("marketing", 400), // +15% income per level
    EXPANSION("expansion", 500), // +4 attraction slots per level
    SECURITY("security", 300);   // reserved: reduces risk/liability in later stages

    public static final int MAX_LEVEL = 5;

    public final String id;
    private final long baseCost;

    UpgradeType(String id, long baseCost) {
        this.id = id;
        this.baseCost = baseCost;
    }

    /** Cost to go from currentLevel to currentLevel + 1. */
    public long costFor(int currentLevel) {
        return baseCost * (1L + (long) currentLevel * currentLevel);
    }

    public String translationKey() {
        return "upgrade.fnaf6." + id;
    }
}
