package com.aharness.registry;

import com.aharness.AvaritiaHarnessBackport;
import com.aharness.item.NeutronHarnessItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class AHBItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, AvaritiaHarnessBackport.MOD_ID);

    public static final RegistryObject<Item> NEUTRON_HARNESS =
            ITEMS.register("neutron_harness", NeutronHarnessItem::new);

    private AHBItems() {
    }

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }
}
