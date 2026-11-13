package com.aharness.net;

import com.aharness.AvaritiaHarnessBackport;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public final class AHBNetwork {
    private static final String PROTOCOL = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            ResourceLocation.fromNamespaceAndPath(AvaritiaHarnessBackport.MOD_ID, "main"),
            () -> PROTOCOL,
            PROTOCOL::equals,
            PROTOCOL::equals
    );

    private AHBNetwork() {
    }

    public static void register() {
        CHANNEL.registerMessage(
                0,
                C2SHarnessActionPacket.class,
                C2SHarnessActionPacket::encode,
                C2SHarnessActionPacket::decode,
                C2SHarnessActionPacket::handle
        );
    }

    public static void sendToServer(C2SHarnessActionPacket packet) {
        CHANNEL.sendToServer(packet);
    }
}
