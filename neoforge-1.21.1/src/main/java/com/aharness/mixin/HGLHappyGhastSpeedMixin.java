package com.aharness.mixin;

import com.aharness.AvaritiaHarnessBackport;
import com.aharness.compat.HGLMixinState;
import com.aharness.registry.AHBItems;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * Happy Ghast Legacy uses a hardcoded 0.09F travel speed and never reads
 * {@code Attributes.FLYING_SPEED}. This soft mixin scales that constant while
 * the neutron harness is in the CHEST slot, matching the +0.95 attribute bonus
 * against HGL's 0.32 base flying speed.
 *
 * <p>{@code @Pseudo} + {@code require = 0} make the mixin a no-op when HGL is
 * absent or changes its travel implementation.</p>
 */
@Pseudo
@Mixin(targets = "com.iafenvoy.ghast.entity.HappyGhastEntity")
public abstract class HGLHappyGhastSpeedMixin {
    private static final float SPEED_MULTIPLIER = (0.32F + 0.45F) / 0.32F;

    @ModifyConstant(method = "travel", constant = @Constant(floatValue = 0.09F), require = 0)
    private float ahb$applyHarnessSpeed(float original) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (!(self.getFirstPassenger() instanceof Player)
                || !self.getItemBySlot(EquipmentSlot.CHEST).is(AHBItems.NEUTRON_HARNESS.get())) {
            return original;
        }
        if (!HGLMixinState.speedApplied) {
            HGLMixinState.speedApplied = true;
            AvaritiaHarnessBackport.LOGGER.info("HGL harness speed mixin applied (x{})", SPEED_MULTIPLIER);
        }
        return original * SPEED_MULTIPLIER;
    }
}
