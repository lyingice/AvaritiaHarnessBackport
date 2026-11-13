package com.aharness;

import com.aharness.compat.HGLCompat;
import com.aharness.registry.AHBItems;
import com.mojang.logging.LogUtils;
import committee.nova.mods.avaritia.init.registry.ModCreativeModeTabs;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(AvaritiaHarnessBackport.MOD_ID)
public final class AvaritiaHarnessBackport {
    public static final String MOD_ID = "avaritia_harness_backport";
    public static final Logger LOGGER = LogUtils.getLogger();

    public AvaritiaHarnessBackport() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        AHBItems.register(modEventBus);
        modEventBus.addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.addListener(HGLCompat::onEntityInteract);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> ModCreativeModeTabs.ACCEPT_ITEM.add(AHBItems.NEUTRON_HARNESS));
    }
}
