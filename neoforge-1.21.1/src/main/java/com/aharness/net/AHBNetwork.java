package com.aharness.net;

import com.aharness.AvaritiaHarnessBackport;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

@EventBusSubscriber(modid = AvaritiaHarnessBackport.MOD_ID)
public final class AHBNetwork {
    private AHBNetwork() {
    }

    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        event.registrar("1").playToServer(
                C2SHarnessActionPacket.TYPE,
                C2SHarnessActionPacket.STREAM_CODEC,
                new C2SHarnessActionPacket.Handler()
        );
    }

    public static void sendToServer(C2SHarnessActionPacket packet) {
        PacketDistributor.sendToServer(packet);
    }
}
