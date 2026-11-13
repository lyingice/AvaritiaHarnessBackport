package com.aharness.gametest;

import com.aharness.AvaritiaHarnessBackport;
import com.aharness.item.NeutronHarnessItem;
import com.aharness.compat.HGLMixinState;
import com.aharness.registry.AHBItems;
import committee.nova.mods.avaritia.common.crafting.input.ExtremeSmithingRecipeInput;
import committee.nova.mods.avaritia.common.crafting.recipe.ExtremeSmithingRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(AvaritiaHarnessBackport.MOD_ID)
@PrefixGameTestTemplate(false)
public final class AHBCompatGameTests {
    private AHBCompatGameTests() {
    }

    @GameTest(template = "ahb_empty", timeoutTicks = 60)
    public static void vanillaBackportGhastAcceptsHarness(GameTestHelper helper) {
        assertHarnessAccepted(helper, ResourceLocation.fromNamespaceAndPath("minecraft", "happy_ghast"));
    }

    @GameTest(template = "ahb_empty", timeoutTicks = 60)
    public static void happyGhastLegacyGhastAcceptsHarness(GameTestHelper helper) {
        assertHarnessAccepted(helper, ResourceLocation.fromNamespaceAndPath("happy_ghast_legacy", "happy_ghast"));
    }

    @GameTest(template = "ahb_empty", timeoutTicks = 60)
    public static void recipeMatchesAndAssembles(GameTestHelper helper) {
        var holder = helper.getLevel().getRecipeManager()
                .byKey(ResourceLocation.fromNamespaceAndPath(AvaritiaHarnessBackport.MOD_ID, "neutron_harness"));
        if (holder.isEmpty() || !(holder.get().value() instanceof ExtremeSmithingRecipe recipe)) {
            helper.fail("neutron_harness extreme smithing recipe is missing or has the wrong type");
            return;
        }

        Item template = BuiltInRegistries.ITEM.get(
                ResourceLocation.fromNamespaceAndPath("avaritia", "upgrade_smithing_template"));
        Item base = BuiltInRegistries.ITEM.get(
                ResourceLocation.fromNamespaceAndPath("minecraft", "white_harness"));
        Item core = BuiltInRegistries.ITEM.get(
                ResourceLocation.fromNamespaceAndPath("avaritia", "enhancement_core"));
        if (template == Items.AIR || base == Items.AIR || core == Items.AIR) {
            helper.fail("Recipe inputs are missing (template/base/core)");
            return;
        }

        ExtremeSmithingRecipeInput input = new ExtremeSmithingRecipeInput(
                new ItemStack(template),
                new ItemStack(base),
                PotionContents.createItemStack(Items.POTION, Potions.SLOW_FALLING),
                new ItemStack(core),
                new ItemStack(Items.FEATHER)
        );

        helper.assertTrue(recipe.matches(input, helper.getLevel()),
                "Recipe must match a colored harness + slow falling potion + enhancement core + feather");
        helper.assertTrue(recipe.assemble(input, helper.getLevel().registryAccess()).is(AHBItems.NEUTRON_HARNESS.get()),
                "Recipe must output the neutron harness");
        helper.succeed();
    }

    @GameTest(template = "ahb_empty", timeoutTicks = 60)
    public static void hglSpeedMixinApplies(GameTestHelper helper) {
        ResourceLocation entityId = ResourceLocation.fromNamespaceAndPath("happy_ghast_legacy", "happy_ghast");
        EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.get(entityId);
        if (!entityId.equals(BuiltInRegistries.ENTITY_TYPE.getKey(type))) {
            helper.fail("Missing happy ghast entity type: " + entityId);
            return;
        }

        Entity created = type.create(helper.getLevel());
        if (!(created instanceof LivingEntity living)) {
            helper.fail("Entity is not a LivingEntity: " + entityId);
            return;
        }

        living.moveTo(helper.absolutePos(new BlockPos(1, 2, 1)), 0.0F, 0.0F);
        helper.getLevel().addFreshEntity(living);

        Item harness = BuiltInRegistries.ITEM.get(
                ResourceLocation.fromNamespaceAndPath(AvaritiaHarnessBackport.MOD_ID, "neutron_harness"));
        living.setItemSlot(EquipmentSlot.CHEST, new ItemStack(harness));

        HGLMixinState.speedApplied = false;
        living.travel(Vec3.ZERO);

        helper.assertTrue(HGLMixinState.speedApplied,
                "HGL speed mixin must apply when the harness is equipped in the CHEST slot");
        helper.succeed();
    }

    @GameTest(template = "ahb_empty", timeoutTicks = 60)
    public static void hglInteractionEquipsHarness(GameTestHelper helper) {
        ResourceLocation entityId = ResourceLocation.fromNamespaceAndPath("happy_ghast_legacy", "happy_ghast");
        EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.get(entityId);
        if (!entityId.equals(BuiltInRegistries.ENTITY_TYPE.getKey(type))) {
            helper.fail("Missing happy ghast entity type: " + entityId);
            return;
        }

        Entity created = type.create(helper.getLevel());
        if (!(created instanceof LivingEntity living)) {
            helper.fail("Entity is not a LivingEntity: " + entityId);
            return;
        }

        living.moveTo(helper.absolutePos(new BlockPos(1, 2, 1)), 0.0F, 0.0F);
        helper.getLevel().addFreshEntity(living);

        Item harness = BuiltInRegistries.ITEM.get(
                ResourceLocation.fromNamespaceAndPath(AvaritiaHarnessBackport.MOD_ID, "neutron_harness"));
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        player.moveTo(living.getX(), living.getY(), living.getZ(), 0.0F, 0.0F);
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(harness));
        player.interactOn(living, InteractionHand.MAIN_HAND);

        helper.runAfterDelay(5, () -> {
            helper.assertTrue(living.getItemBySlot(EquipmentSlot.CHEST).is(harness),
                    "Right-clicking an HGL ghast must equip the harness through HGLCompat");
            helper.succeed();
        });
    }

    private static void assertHarnessAccepted(GameTestHelper helper, ResourceLocation entityId) {
        EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.get(entityId);
        if (!entityId.equals(BuiltInRegistries.ENTITY_TYPE.getKey(type))) {
            helper.fail("Missing happy ghast entity type: " + entityId);
            return;
        }

        Entity created = type.create(helper.getLevel());
        if (!(created instanceof LivingEntity living)) {
            helper.fail("Entity is not a LivingEntity: " + entityId);
            return;
        }

        living.moveTo(helper.absolutePos(new BlockPos(1, 2, 1)), 0.0F, 0.0F);
        helper.getLevel().addFreshEntity(living);

        Item harness = BuiltInRegistries.ITEM.get(
                ResourceLocation.fromNamespaceAndPath(AvaritiaHarnessBackport.MOD_ID, "neutron_harness"));
        if (harness == Items.AIR) {
            helper.fail("neutron_harness is not registered");
            return;
        }

        living.setItemSlot(EquipmentSlot.CHEST, new ItemStack(harness));
        helper.runAfterDelay(5, () -> {
            helper.assertTrue(living.getItemBySlot(EquipmentSlot.CHEST).is(harness),
                    "Harness must stay in the CHEST slot for " + entityId);

            AttributeInstance armor = living.getAttribute(Attributes.ARMOR);
            AttributeModifier armorModifier = armor == null ? null : armor.getModifier(NeutronHarnessItem.ARMOR_ID);
            helper.assertTrue(armorModifier != null,
                    "Harness armor modifier must be applied for " + entityId);
            helper.assertTrue(armorModifier.amount() == 50.0D,
                    "Harness armor modifier must be +50 for " + entityId
                            + " (got " + armorModifier.amount() + ")");

            AttributeInstance toughness = living.getAttribute(Attributes.ARMOR_TOUGHNESS);
            AttributeModifier toughnessModifier =
                    toughness == null ? null : toughness.getModifier(NeutronHarnessItem.TOUGHNESS_ID);
            helper.assertTrue(toughnessModifier != null && toughnessModifier.amount() == 10.0D,
                    "Harness toughness modifier must be +10 for " + entityId);

            AttributeInstance flyingSpeed = living.getAttribute(Attributes.FLYING_SPEED);
            AttributeModifier flyingSpeedModifier =
                    flyingSpeed == null ? null : flyingSpeed.getModifier(NeutronHarnessItem.FLYING_SPEED_ID);
            helper.assertTrue(flyingSpeedModifier != null && flyingSpeedModifier.amount() == 0.95D,
                    "Harness flying speed modifier must be +0.95 for " + entityId);

            // Attributes.ARMOR is a RangedAttribute capped at 30 in 1.20.1/1.21.1,
            // so the effective value is 30 even though the item grants +50.
            helper.assertTrue(living.getAttributeValue(Attributes.ARMOR) >= 30.0D,
                    "Harness must reach the armor cap for " + entityId
                            + " (got " + living.getAttributeValue(Attributes.ARMOR) + ")");
            helper.succeed();
        });
    }
}
