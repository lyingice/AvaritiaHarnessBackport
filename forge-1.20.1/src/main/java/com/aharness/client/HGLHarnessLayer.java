package com.aharness.client;

import com.aharness.AvaritiaHarnessBackport;
import com.aharness.registry.AHBItems;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class HGLHarnessLayer<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {
    public static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(AvaritiaHarnessBackport.MOD_ID, "textures/entity/neutron_harness.png");

    private final NeutronHarnessModel model;

    public HGLHarnessLayer(RenderLayerParent<T, M> renderer, NeutronHarnessModel model) {
        super(renderer);
        this.model = model;
    }

    @Override
    public void render(PoseStack pose, MultiBufferSource buffer, int packedLight, T entity,
                       float limbSwing, float limbSwingAmount, float partialTick,
                       float ageInTicks, float netHeadYaw, float headPitch) {
        ItemStack stack = entity.getItemBySlot(EquipmentSlot.CHEST);
        if (entity.isBaby() || !stack.is(AHBItems.NEUTRON_HARNESS.get())) {
            return;
        }

        pose.pushPose();
        pose.scale(1.05F, 1.05F, 1.05F);
        pose.translate(0.0D, 0.05D, 0.0D);
        this.model.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
        this.model.renderToBuffer(pose, consumer, packedLight, OverlayTexture.NO_OVERLAY,
                1.0F, 1.0F, 1.0F, 1.0F);
        pose.popPose();
    }
}
