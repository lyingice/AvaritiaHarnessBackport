package com.aharness.client;

import com.aharness.AvaritiaHarnessBackport;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = AvaritiaHarnessBackport.MOD_ID,
        bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientEvents {
    public static final ModelLayerLocation HARNESS_LAYER = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(AvaritiaHarnessBackport.MOD_ID, "neutron_harness"), "main");

    private static final ResourceLocation HGL_GHAST_ID =
            ResourceLocation.fromNamespaceAndPath("happy_ghast_legacy", "happy_ghast");

    private ClientEvents() {
    }

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(HARNESS_LAYER, NeutronHarnessModel::createLayer);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @SubscribeEvent
    public static void addLayers(EntityRenderersEvent.AddLayers event) {
        EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.get(HGL_GHAST_ID);
        if (!HGL_GHAST_ID.equals(BuiltInRegistries.ENTITY_TYPE.getKey(type))) {
            return; // Happy Ghast Legacy is not installed.
        }

        EntityRenderer<?> renderer = event.getRenderer((EntityType) type);
        if (!(renderer instanceof LivingEntityRenderer livingRenderer)) {
            return;
        }

        NeutronHarnessModel model = new NeutronHarnessModel(
                Minecraft.getInstance().getEntityModels().bakeLayer(HARNESS_LAYER));
        livingRenderer.addLayer((RenderLayer) new HGLHarnessLayer((RenderLayerParent) livingRenderer, model));
    }
}
