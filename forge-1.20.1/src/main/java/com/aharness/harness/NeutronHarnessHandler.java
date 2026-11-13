package com.aharness.harness;

import com.aharness.AvaritiaHarnessBackport;
import com.aharness.config.AHBConfig;
import com.aharness.item.NeutronHarnessItem;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 26.1.2 harness mechanics for Forge 1.20.1.
 */
@Mod.EventBusSubscriber(modid = AvaritiaHarnessBackport.MOD_ID)
public final class NeutronHarnessHandler {
    private static final int SHIELD_PARTICLE_POINTS = 16;
    private static final double GOLDEN_ANGLE = Math.PI * (3.0D - Math.sqrt(5.0D));
    private static final Map<UUID, ActiveShield> SHIELDS = new HashMap<>();

    private NeutronHarnessHandler() {
    }

    private record ActiveShield(Player owner, Entity anchor, long until) {
    }

    public static boolean summonMount(ServerPlayer player) {
        if (!(player.level() instanceof ServerLevel level) || player.isPassenger() || player.onGround()) {
            return false;
        }
        LivingEntity ghast = findNearestArmoredGhast(level, player);
        if (ghast == null) {
            failFeedback(level, player);
            return false;
        }

        double originX = ghast.getX();
        double originY = ghast.getY();
        double originZ = ghast.getZ();
        ghast.setPos(player.getX(), player.getY() - ghast.getBbHeight() - 0.05D, player.getZ());
        ghast.setYRot(player.getYRot());
        ghast.setDeltaMovement(Vec3.ZERO);
        if (!level.noCollision(ghast)) {
            ghast.setPos(originX, originY, originZ);
            failFeedback(level, player);
            return false;
        }

        if (HarnessData.isUnowned(ghast)) {
            HarnessData.setOwner(ghast, player.getUUID());
        }
        player.startRiding(ghast, true);
        level.playSound(null, ghast.getX(), ghast.getY(), ghast.getZ(),
                SoundEvents.ARMOR_EQUIP_LEATHER, SoundSource.NEUTRAL, 1.0F, 1.0F);
        return player.getVehicle() == ghast;
    }

    private static LivingEntity findNearestArmoredGhast(ServerLevel level, ServerPlayer player) {
        LivingEntity owned = null;
        LivingEntity unowned = null;
        double ownedDistance = Double.MAX_VALUE;
        double unownedDistance = Double.MAX_VALUE;
        for (Entity entity : level.getAllEntities()) {
            if (!(entity instanceof LivingEntity living) || !HarnessData.isHarnessedHappyGhast(living)
                    || living.isRemoved() || !living.getPassengers().isEmpty()) {
                continue;
            }
            double distance = living.distanceToSqr(player);
            UUID owner = HarnessData.getOwner(living);
            if (owner != null && owner.equals(player.getUUID())) {
                if (distance < ownedDistance) {
                    ownedDistance = distance;
                    owned = living;
                }
            } else if (owner == null && distance < unownedDistance) {
                unownedDistance = distance;
                unowned = living;
            }
        }
        return owned != null ? owned : unowned;
    }

    public static void openShield(ServerPlayer player) {
        if (!(player.level() instanceof ServerLevel level)) {
            return;
        }
        Entity anchor = player.getVehicle();
        if (!(anchor instanceof LivingEntity living) || !HarnessData.isHarnessedHappyGhast(living)) {
            failFeedback(level, player);
            return;
        }

        long gameTime = level.getGameTime();
        if (HarnessData.getShieldCooldownUntil(player) > gameTime) {
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.NOTE_BLOCK_BASS.value(), SoundSource.PLAYERS, 0.8F, 0.6F);
            return;
        }

        long duration = Math.max(1, AHBConfig.SHIELD_DURATION_SECONDS.get()) * 20L;
        long cooldown = Math.max(1, AHBConfig.SHIELD_COOLDOWN_SECONDS.get()) * 20L;
        SHIELDS.put(player.getUUID(), new ActiveShield(player, anchor, gameTime + duration));
        HarnessData.setShieldState(player, gameTime + duration, gameTime + cooldown);
        level.playSound(null, anchor.getX(), anchor.getY(), anchor.getZ(),
                SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 1.0F, 1.2F);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onAttack(LivingAttackEvent event) {
        if (event.getEntity().level().isClientSide()) {
            return;
        }
        if (!SHIELDS.isEmpty() && isProtected(event.getEntity())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onHurt(LivingHurtEvent event) {
        if (event.getEntity().level().isClientSide()) {
            return;
        }
        if (!SHIELDS.isEmpty() && isProtected(event.getEntity())) {
            event.setAmount(0.0F);
            return;
        }
        if (HarnessData.isHarnessedHappyGhast(event.getEntity())) {
            event.setAmount(modifyIncomingDamage(event.getEntity(), event.getAmount()));
        }
    }

    public static float modifyIncomingDamage(LivingEntity target, float amount) {
        if (!HarnessData.isHarnessedHappyGhast(target)) {
            return amount;
        }
        return (float) (amount * (1.0D - AHBConfig.GHAST_DAMAGE_REDUCTION.get()));
    }

    @SubscribeEvent
    public static void onMount(EntityMountEvent event) {
        if (!event.isMounting() || event.getLevel().isClientSide()) {
            return;
        }
        if (!(event.getEntityMounting() instanceof ServerPlayer player)) {
            return;
        }
        if (!(event.getEntityBeingMounted() instanceof LivingEntity ghast)
                || !HarnessData.isHarnessedHappyGhast(ghast)) {
            return;
        }
        if (HarnessData.isUnowned(ghast)) {
            HarnessData.setOwner(ghast, player.getUUID());
        }
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || SHIELDS.isEmpty()) {
            return;
        }
        SHIELDS.values().removeIf(shield -> shield.owner().isRemoved()
                || shield.anchor().isRemoved()
                || shield.owner().level() != shield.anchor().level()
                || shield.until() <= shield.anchor().level().getGameTime());
        for (ActiveShield shield : SHIELDS.values()) {
            drawShield(shield.anchor());
        }
    }

    @SubscribeEvent
    public static void onServerStopped(ServerStoppedEvent event) {
        SHIELDS.clear();
    }

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity living = event.getEntity();
        if (living.level().isClientSide() || !HarnessData.isSupportedHappyGhast(living)
                || living.tickCount % 20 != 0) {
            return;
        }
        updateRidingSpeed(living);
    }

    /**
     * The 26.1.2 item has no base flying speed modifier; the handler adds +0.45
     * only while a player is riding the harnessed ghast.
     */
    public static void updateRidingSpeed(LivingEntity ghast) {
        AttributeInstance flying = ghast.getAttribute(Attributes.FLYING_SPEED);
        if (flying == null) {
            return;
        }
        flying.removeModifier(NeutronHarnessItem.FLYING_SPEED_UUID);
        if (HarnessData.isHarnessedHappyGhast(ghast) && ghast.getFirstPassenger() instanceof Player) {
            flying.addTransientModifier(new AttributeModifier(
                    NeutronHarnessItem.FLYING_SPEED_UUID,
                    "Neutron harness riding speed",
                    0.45D,
                    AttributeModifier.Operation.ADDITION
            ));
        }
    }

    public static boolean isEntityProtected(LivingEntity target) {
        return isProtected(target);
    }

    public static void registerShieldForTest(Player player, Entity anchor, long durationTicks) {
        SHIELDS.put(player.getUUID(),
                new ActiveShield(player, anchor, player.level().getGameTime() + durationTicks));
    }

    public static void clearShieldsForTest() {
        SHIELDS.clear();
    }

    private static boolean isProtected(LivingEntity target) {
        long gameTime = target.level().getGameTime();
        double radius = AHBConfig.SHIELD_RADIUS.get();
        for (ActiveShield shield : SHIELDS.values()) {
            Entity anchor = shield.anchor();
            if (gameTime >= shield.until() || anchor.isRemoved() || anchor.level() != target.level()) {
                continue;
            }
            if (target == anchor || target.getRootVehicle() == anchor) {
                return true;
            }
            if (!(target instanceof Enemy) && target.distanceToSqr(anchor) <= radius * radius) {
                return true;
            }
        }
        return false;
    }

    private static void drawShield(Entity anchor) {
        if (!(anchor.level() instanceof ServerLevel level)) {
            return;
        }
        double radius = AHBConfig.SHIELD_RADIUS.get();
        double centerX = anchor.getX();
        double centerY = anchor.getY() + anchor.getBbHeight() * 0.5D;
        double centerZ = anchor.getZ();
        for (int index = 0; index < SHIELD_PARTICLE_POINTS; index++) {
            double y = 1.0D - 2.0D * index / (SHIELD_PARTICLE_POINTS - 1);
            double ring = Math.sqrt(Math.max(0.0D, 1.0D - y * y));
            double theta = index * GOLDEN_ANGLE;
            level.sendParticles(ParticleTypes.END_ROD,
                    centerX + Math.cos(theta) * ring * radius,
                    centerY + y * radius,
                    centerZ + Math.sin(theta) * ring * radius,
                    1, 0.0D, 0.0D, 0.0D, 0.0D);
        }
    }

    private static void failFeedback(ServerLevel level, ServerPlayer player) {
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.VILLAGER_NO, SoundSource.PLAYERS, 0.8F, 1.0F);
    }
}
