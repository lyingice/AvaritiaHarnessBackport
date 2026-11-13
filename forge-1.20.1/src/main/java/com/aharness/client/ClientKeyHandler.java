package com.aharness.client;

import com.aharness.AvaritiaHarnessBackport;
import com.aharness.net.AHBNetwork;
import com.aharness.net.C2SHarnessActionPacket;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = AvaritiaHarnessBackport.MOD_ID, value = Dist.CLIENT)
public final class ClientKeyHandler {
    private ClientKeyHandler() {
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        while (ClientKeybinds.SUMMON.consumeClick()) {
            if (minecraft.player != null) {
                AHBNetwork.sendToServer(new C2SHarnessActionPacket(C2SHarnessActionPacket.ACTION_SUMMON));
            }
        }
        while (ClientKeybinds.SHIELD.consumeClick()) {
            if (minecraft.player != null) {
                AHBNetwork.sendToServer(new C2SHarnessActionPacket(C2SHarnessActionPacket.ACTION_SHIELD));
            }
        }
    }
}
