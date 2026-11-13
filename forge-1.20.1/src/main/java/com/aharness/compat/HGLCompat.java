package com.aharness.compat;

import com.aharness.registry.AHBItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

/**
 * Happy Ghast Legacy compatibility (1.20.1 Forge).
 *
 * <p>HGL only accepts its own HarnessItem class, so the harness is written
 * directly into the CHEST slot that HGL already uses for body armor. Rendering
 * is handled by {@code HGLHarnessLayer}.</p>
 */
public final class HGLCompat {
    private static final ResourceLocation HGL_GHAST_ID =
            ResourceLocation.fromNamespaceAndPath("happy_ghast_legacy", "happy_ghast");

    private HGLCompat() {
    }

    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        Entity target = event.getTarget();
        if (!(target instanceof LivingEntity living)) {
            return;
        }
        if (!isHappyGhastFromHGL(living)) {
            return;
        }
        if (living.isBaby() || !living.getItemBySlot(EquipmentSlot.CHEST).isEmpty()) {
            return;
        }

        Player player = event.getEntity();
        ItemStack stack = event.getItemStack();
        if (!stack.is(AHBItems.NEUTRON_HARNESS.get())) {
            return;
        }

        if (!player.level().isClientSide) {
            living.setItemSlot(EquipmentSlot.CHEST, stack.copyWithCount(1));
            if (!player.isCreative()) {
                stack.shrink(1);
            }
            living.playSound(SoundEvents.ARMOR_EQUIP_LEATHER, 1.0F, 1.0F);
        }

        event.setCancellationResult(InteractionResult.SUCCESS);
        event.setCanceled(true);
    }

    private static boolean isHappyGhastFromHGL(LivingEntity entity) {
        EntityType<?> type = entity.getType();
        return HGL_GHAST_ID.equals(BuiltInRegistries.ENTITY_TYPE.getKey(type));
    }
}
