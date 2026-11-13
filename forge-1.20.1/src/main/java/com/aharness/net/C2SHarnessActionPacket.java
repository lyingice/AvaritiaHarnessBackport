package com.aharness.net;

import com.aharness.harness.NeutronHarnessHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record C2SHarnessActionPacket(int action) {
    public static final int ACTION_SUMMON = 0;
    public static final int ACTION_SHIELD = 1;

    public static void encode(C2SHarnessActionPacket packet, FriendlyByteBuf buffer) {
        buffer.writeVarInt(packet.action);
    }

    public static C2SHarnessActionPacket decode(FriendlyByteBuf buffer) {
        return new C2SHarnessActionPacket(buffer.readVarInt());
    }

    public static void handle(C2SHarnessActionPacket packet, Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) {
                return;
            }
            switch (packet.action) {
                case ACTION_SUMMON -> NeutronHarnessHandler.summonMount(player);
                case ACTION_SHIELD -> NeutronHarnessHandler.openShield(player);
                default -> {
                }
            }
        });
        context.setPacketHandled(true);
    }
}
