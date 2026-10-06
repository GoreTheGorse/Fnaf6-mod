package com.fnaf6.mod.item;

import com.fnaf6.mod.Fnaf6Mod;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {
    public static final Item FAZ_COIN = register("faz_coin", new FazCoinItem(new Item.Settings()));
    public static final Item FAZ_TABLET = register("faz_tablet", new FazTabletItem(new Item.Settings().maxCount(1)));

    private static Item register(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(Fnaf6Mod.MOD_ID, name), item);
    }

    /** Forces class loading so the static fields above register at the right time. */
    public static void register() {
        Fnaf6Mod.LOGGER.debug("Registered items");
    }
}
