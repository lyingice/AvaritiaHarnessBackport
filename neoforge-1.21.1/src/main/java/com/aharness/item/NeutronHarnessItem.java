package com.aharness.item;

import committee.nova.mods.avaritia.api.common.enchant.InitEnchantment;
import committee.nova.mods.avaritia.api.iface.item.InitEnchantItem;
import committee.nova.mods.avaritia.common.entity.ImmortalItemEntity;
import committee.nova.mods.avaritia.init.registry.ModEntities;
import committee.nova.mods.avaritia.init.registry.ModRarities;
import com.aharness.AvaritiaHarnessBackport;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * 星空挽具 / Neutron Harness.
 *
 * <p>1.20.1 and 1.21.1 do not have the vanilla BODY equipment slot used by the
 * modern happy ghast. Both backport mods store the harness in the CHEST slot,
 * so the CHEST slot group is used for all attribute modifiers.</p>
 */
public class NeutronHarnessItem extends Item implements InitEnchantItem {
    public static final ResourceLocation ARMOR_ID =
            ResourceLocation.fromNamespaceAndPath(AvaritiaHarnessBackport.MOD_ID, "neutron_harness_armor");
    public static final ResourceLocation TOUGHNESS_ID =
            ResourceLocation.fromNamespaceAndPath(AvaritiaHarnessBackport.MOD_ID, "neutron_harness_armor_toughness");
    public static final ResourceLocation FLYING_SPEED_ID =
            ResourceLocation.fromNamespaceAndPath(AvaritiaHarnessBackport.MOD_ID, "neutron_harness_flying_speed");

    private final InitEnchantment allDamageProtection = new InitEnchantment(Enchantments.PROTECTION, 10);

    public NeutronHarnessItem() {
        super(new Item.Properties()
                .stacksTo(1)
                .rarity(ModRarities.RARE)
                .fireResistant()
                .setNoRepair()
                .attributes(ItemAttributeModifiers.builder()
                        .add(Attributes.ARMOR,
                                new AttributeModifier(ARMOR_ID, 50.0D, AttributeModifier.Operation.ADD_VALUE),
                                EquipmentSlotGroup.CHEST)
                        .add(Attributes.ARMOR_TOUGHNESS,
                                new AttributeModifier(TOUGHNESS_ID, 10.0D, AttributeModifier.Operation.ADD_VALUE),
                                EquipmentSlotGroup.CHEST)
                        .add(Attributes.FLYING_SPEED,
                                new AttributeModifier(FLYING_SPEED_ID, 0.95D, AttributeModifier.Operation.ADD_VALUE),
                                EquipmentSlotGroup.CHEST)
                        .build()));
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
        return ImmortalItemEntity.create(
                ModEntities.IMMORTAL.get(),
                level,
                location.getX(),
                location.getY(),
                location.getZ(),
                stack.copy()
        );
    }

    @Override
    public int getInitEnchantLevel(ItemStack stack, Holder<Enchantment> enchantment) {
        return enchantment.is(Enchantments.PROTECTION) ? 10 : 0;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context,
                                List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        this.allDamageProtection.appendHoverText(context, tooltipComponents);
    }
}
