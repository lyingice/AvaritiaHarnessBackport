package com.aharness.gametest;

import com.aharness.AvaritiaHarnessBackport;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(AvaritiaHarnessBackport.MOD_ID)
@PrefixGameTestTemplate(false)
public final class AHBRegistrationGameTests {
    private AHBRegistrationGameTests() {
    }

    @GameTest(template = "ahb_empty")
    public static void itemTagsAndRecipeRegistered(GameTestHelper helper) {
        Item item = BuiltInRegistries.ITEM.get(
                ResourceLocation.fromNamespaceAndPath(AvaritiaHarnessBackport.MOD_ID, "neutron_harness"));
        helper.assertTrue(item != Items.AIR, "neutron_harness must be registered");

        ItemStack stack = new ItemStack(item);
        helper.assertTrue(
                stack.is(TagKey.create(Registries.ITEM,
                        ResourceLocation.fromNamespaceAndPath("minecraft", "harnesses"))),
                "neutron_harness must be in minecraft:harnesses");
        helper.assertTrue(
                stack.is(TagKey.create(Registries.ITEM,
                        ResourceLocation.fromNamespaceAndPath("happy_ghast_legacy", "harness"))),
                "neutron_harness must be in happy_ghast_legacy:harness");

        helper.assertTrue(
                helper.getLevel().getRecipeManager()
                        .byKey(ResourceLocation.fromNamespaceAndPath(AvaritiaHarnessBackport.MOD_ID, "neutron_harness"))
                        .isPresent(),
                "neutron_harness recipe must load");

        Holder<Enchantment> protection = helper.getLevel().registryAccess()
                .lookupOrThrow(Registries.ENCHANTMENT)
                .getOrThrow(Enchantments.PROTECTION);
        int protectionLevel = EnchantmentHelper.getItemEnchantmentLevel(protection, stack);
        helper.assertTrue(protectionLevel == 10,
                "neutron_harness must provide Protection X (got " + protectionLevel + ")");

        helper.succeed();
    }
}
