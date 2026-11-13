package com.aharness.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import committee.nova.mods.avaritia.api.iface.InitEnchantItem;
import committee.nova.mods.avaritia.common.entity.ImmortalItemEntity;
import committee.nova.mods.avaritia.init.registry.ModEntities;
import committee.nova.mods.avaritia.init.registry.ModRarities;
import committee.nova.mods.avaritia.init.registry.ModTooltips;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

/**
 * 星空挽具 / Neutron Harness (1.20.1).
 *
 * <p>1.20.1 has no BODY equipment slot. Vanilla Backport and Happy Ghast Legacy
 * both use the CHEST slot for the harness, so the modifiers are attached to
 * {@link EquipmentSlot#CHEST}.</p>
 */
public class NeutronHarnessItem extends Item implements InitEnchantItem {
    public static final UUID ARMOR_UUID = UUID.fromString("1a2b3c4d-5e6f-4a7b-8c9d-0e1f2a3b4c5d");
    public static final UUID TOUGHNESS_UUID = UUID.fromString("2b3c4d5e-6f7a-4b8c-9d0e-1f2a3b4c5d6e");
    public static final UUID FLYING_SPEED_UUID = UUID.fromString("3c4d5e6f-7a8b-4c9d-0e1f-2a3b4c5d6e7f");

    public NeutronHarnessItem() {
        super(new Item.Properties()
                .stacksTo(1)
                .rarity(ModRarities.RARE)
                .fireResistant()
                .setNoRepair());
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
        if (slot != EquipmentSlot.CHEST) {
            return super.getDefaultAttributeModifiers(slot);
        }

        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.putAll(super.getDefaultAttributeModifiers(slot));
        builder.put(Attributes.ARMOR, new AttributeModifier(
                ARMOR_UUID, "Neutron harness armor", 50.0D, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(
                TOUGHNESS_UUID, "Neutron harness toughness", 10.0D, AttributeModifier.Operation.ADDITION));
        return builder.build();
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return false;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

    @Override
    public int getEnchantmentValue(ItemStack stack) {
        return 10;
    }

    @Override
    public boolean hasCustomEntity(ItemStack stack) {
        return true;
    }

    @Nullable
    @Override
    public Entity createEntity(Level level, Entity location, ItemStack stack) {
        return ImmortalItemEntity.create(ModEntities.IMMORTAL.get(), level, location, stack);
    }

    @Override
    public int getInitEnchantLevel(ItemStack stack, Enchantment enchantment) {
        return enchantment == Enchantments.ALL_DAMAGE_PROTECTION ? 10 : 0;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level,
                                List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(ModTooltips.INIT_ENCHANT
                .args(Enchantments.ALL_DAMAGE_PROTECTION.getFullname(10))
                .build());
    }
}
