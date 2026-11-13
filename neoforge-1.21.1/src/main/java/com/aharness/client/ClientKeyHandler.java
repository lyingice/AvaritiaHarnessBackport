package com.aharness.client;

import com.aharness.AvaritiaHarnessBackport;
import com.aharness.net.AHBNetwork;
import com.aharness.net.C2SHarnessActionPacket;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

@EventBusSubscriber(modid = AvaritiaHarnessBackport.MOD_ID, value = Dist.CLIENT)
public final class ClientKeyHandler {
    private ClientKeyHandler() {
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
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
