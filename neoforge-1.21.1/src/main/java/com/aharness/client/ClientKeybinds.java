package com.aharness.client;

import com.aharness.AvaritiaHarnessBackport;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid = AvaritiaHarnessBackport.MOD_ID, value = Dist.CLIENT)
public final class ClientKeybinds {
    public static final String CATEGORY = "key.categories.avaritia_harness_backport";

    /** Unbound by default; the player assigns it in Controls. */
    public static final KeyMapping SUMMON = new KeyMapping(
            "key.avaritia_harness_backport.summon",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_UNKNOWN,
            CATEGORY
    );

    /** Unbound by default; the player assigns it in Controls. */
    public static final KeyMapping SHIELD = new KeyMapping(
            "key.avaritia_harness_backport.shield",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_UNKNOWN,
            CATEGORY
    );

    private ClientKeybinds() {
    }

    @SubscribeEvent
    public static void register(RegisterKeyMappingsEvent event) {
        event.register(SUMMON);
        event.register(SHIELD);
    }
}
