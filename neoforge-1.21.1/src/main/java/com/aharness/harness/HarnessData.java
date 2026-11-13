package com.aharness.harness;

import com.aharness.registry.AHBItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.Set;
import java.util.UUID;

/**
 * Shared persistent data helpers for the 26.1.2 harness mechanics.
 *
 * <p>1.20.1 Forge and 1.21.1 NeoForge do not share the 26.1.2 data attachment
 * registry, so the owner/shield state is stored in the entity's persistent NBT
 * (available on both loaders). The active shield map itself is transient and
 * lives in {@link NeutronHarnessHandler}.</p>
 */
public final class HarnessData {
    public static final String OWNER_KEY = "ahb_harness_owner";
    public static final String SHIELD_UNTIL_KEY = "ahb_shield_until";
    public static final String SHIELD_COOLDOWN_UNTIL_KEY = "ahb_shield_cooldown_until";

    private static final Set<ResourceLocation> HAPPY_GHAST_IDS = Set.of(
            ResourceLocation.fromNamespaceAndPath("minecraft", "happy_ghast"),
            ResourceLocation.fromNamespaceAndPath("happy_ghast_legacy", "happy_ghast")
    );

    public static boolean isSupportedHappyGhast(Entity entity) {
        return entity instanceof LivingEntity
                && HAPPY_GHAST_IDS.contains(BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()));
    }

    public static boolean isHarnessed(LivingEntity entity) {
        return entity.getItemBySlot(EquipmentSlot.CHEST).is(AHBItems.NEUTRON_HARNESS.get());
    }

    public static boolean isHarnessedHappyGhast(Entity entity) {
        return entity instanceof LivingEntity living && isSupportedHappyGhast(entity) && isHarnessed(living);
    }

    public static UUID getOwner(Entity entity) {
        CompoundTag tag = entity.getPersistentData();
        String value = tag.getString(OWNER_KEY);
        if (value.isEmpty()) {
            return null;
        }
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    public static void setOwner(Entity entity, UUID owner) {
        entity.getPersistentData().putString(OWNER_KEY, owner.toString());
    }

    public static boolean isUnowned(Entity entity) {
        return getOwner(entity) == null;
    }

    public static boolean isOwnedBy(Entity entity, UUID playerId) {
        UUID owner = getOwner(entity);
        return owner != null && owner.equals(playerId);
    }

    public static long getShieldUntil(ServerPlayer player) {
        return player.getPersistentData().getLong(SHIELD_UNTIL_KEY);
    }

    public static long getShieldCooldownUntil(ServerPlayer player) {
        return player.getPersistentData().getLong(SHIELD_COOLDOWN_UNTIL_KEY);
    }

    public static void setShieldState(ServerPlayer player, long shieldUntil, long cooldownUntil) {
        CompoundTag tag = player.getPersistentData();
        tag.putLong(SHIELD_UNTIL_KEY, shieldUntil);
        tag.putLong(SHIELD_COOLDOWN_UNTIL_KEY, cooldownUntil);
    }

    public static boolean isRidingHarnessedGhast(Player player) {
        return player.getVehicle() instanceof LivingEntity living && isHarnessedHappyGhast(living);
    }

    private HarnessData() {
    }
}
