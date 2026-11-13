package com.aharness;

import com.aharness.compat.HGLCompat;
import com.aharness.registry.AHBItems;
import com.mojang.logging.LogUtils;
import committee.nova.mods.avaritia.init.registry.ModCreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

@Mod(AvaritiaHarnessBackport.MOD_ID)
public final class AvaritiaHarnessBackport {
    public static final String MOD_ID = "avaritia_harness_backport";
    public static final Logger LOGGER = LogUtils.getLogger();

    public AvaritiaHarnessBackport(IEventBus modEventBus, ModContainer modContainer) {
        AHBItems.register(modEventBus);
        modEventBus.addListener(this::commonSetup);
        NeoForge.EVENT_BUS.addListener(HGLCompat::onEntityInteract);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> ModCreativeModeTabs.ACCEPT_ITEM.add(AHBItems.NEUTRON_HARNESS));
    }
}
