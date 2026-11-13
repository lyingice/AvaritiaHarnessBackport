package com.aharness.registry;

import com.aharness.AvaritiaHarnessBackport;
import com.aharness.item.NeutronHarnessItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class AHBItems {
    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(AvaritiaHarnessBackport.MOD_ID);

    public static final DeferredItem<Item> NEUTRON_HARNESS =
            ITEMS.register("neutron_harness", NeutronHarnessItem::new);

    private AHBItems() {
    }

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }
}
