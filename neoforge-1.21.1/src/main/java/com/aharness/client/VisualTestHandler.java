package com.aharness.client;

import com.aharness.AvaritiaHarnessBackport;
import com.aharness.registry.AHBItems;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

/**
 * Dev-only visual smoke test. Enable with {@code -Dahb.visualTest=true}.
 *
 * <p>It waits for the singleplayer world to load, spawns one Vanilla Backport
 * happy ghast and one Happy Ghast Legacy happy ghast, equips both with the
 * harness, then saves a screenshot. This class is inert unless the system
 * property is set.</p>
 */
@EventBusSubscriber(modid = AvaritiaHarnessBackport.MOD_ID, value = Dist.CLIENT)
public final class VisualTestHandler {
    private static final boolean ENABLED = Boolean.getBoolean("ahb.visualTest");
    private static int ticks;
    private static boolean setupDone;
    private static boolean screenshotDone;

    private VisualTestHandler() {
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        if (!ENABLED) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.getSingleplayerServer() == null) {
            return;
        }

        ticks++;
        if (!setupDone && ticks > 40) {
            setupDone = true;
            setupScene(minecraft);
        } else if (setupDone && !screenshotDone && ticks > 120) {
            screenshotDone = true;
            Screenshot.grab(minecraft.gameDirectory, minecraft.getMainRenderTarget(),
                    message -> AvaritiaHarnessBackport.LOGGER.info("Visual test screenshot: {}", message.getString()));
        }
    }

    private static void setupScene(Minecraft minecraft) {
        MinecraftServer server = minecraft.getSingleplayerServer();
        if (server == null || minecraft.player == null) {
            return;
        }

        ServerLevel level = server.overworld();
        ServerPlayer player = server.getPlayerList().getPlayer(minecraft.player.getUUID());
        if (player == null) {
            return;
        }

        minecraft.options.setCameraType(CameraType.FIRST_PERSON);
        minecraft.options.hideGui = true;
        player.setGameMode(GameType.CREATIVE);

        // Remove ghasts spawned by earlier visual test runs so the screenshot is deterministic.
        server.getCommands().performPrefixedCommand(server.createCommandSourceStack(),
                "kill @e[type=minecraft:happy_ghast]");
        server.getCommands().performPrefixedCommand(server.createCommandSourceStack(),
                "kill @e[type=happy_ghast_legacy:happy_ghast]");

        player.teleportTo(level, 0.0D, 100.0D, 20.0D, 180.0F, 0.0F);

        spawnEquipped(level, ResourceLocation.fromNamespaceAndPath("minecraft", "happy_ghast"), 6.0D, 100.0D, 0.0D);
        spawnEquipped(level, ResourceLocation.fromNamespaceAndPath("happy_ghast_legacy", "happy_ghast"), -6.0D, 100.0D, 0.0D);
    }

    private static void spawnEquipped(ServerLevel level, ResourceLocation typeId, double x, double y, double z) {
        EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.get(typeId);
        if (!typeId.equals(BuiltInRegistries.ENTITY_TYPE.getKey(type))) {
            AvaritiaHarnessBackport.LOGGER.warn("Visual test: missing entity type {}", typeId);
            return;
        }

        Entity entity = type.create(level);
        if (!(entity instanceof LivingEntity living)) {
            return;
        }

        living.moveTo(x, y, z, 0.0F, 0.0F);
        if (living instanceof Mob mob) {
            mob.setNoAi(true);
        }
        living.setSilent(true);
        living.setItemSlot(EquipmentSlot.CHEST, new ItemStack(AHBItems.NEUTRON_HARNESS.get()));
        level.addFreshEntity(living);

        AvaritiaHarnessBackport.LOGGER.info("Visual test: spawned {} at {}", typeId, living.position());
    }
}
