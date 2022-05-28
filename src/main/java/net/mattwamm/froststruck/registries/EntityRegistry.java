package net.mattwamm.froststruck.registries;

import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.fabricmc.fabric.impl.client.rendering.EntityRendererRegistryImpl;
import net.mattwamm.froststruck.Froststruck;
import net.mattwamm.froststruck.client.FroststruckClient;
import net.mattwamm.froststruck.entities.SledEntity;
import net.mattwamm.froststruck.render.models.SledEntityModel;
import net.mattwamm.froststruck.render.renderers.SledEntityRenderer;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.BoatEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class EntityRegistry {
    public static  EntityType<SledEntity> SLED;
    public static void Register(){
        SLED = register("oak_sled",FabricEntityTypeBuilder.<SledEntity>create(SpawnGroup.MISC, SledEntity::new).dimensions(EntityDimensions.fixed(0.90f, 0.50f)).build());
        RegisterRenderers();
    }

    private static void RegisterRenderers(){
        EntityRendererRegistry.INSTANCE.register(SLED, SledEntityRenderer::new);
    }
    public static void RegisterRenderLayers(){
        EntityModelLayerRegistry.registerModelLayer(FroststruckClient.SLED_MODEL_LAYER, SledEntityModel::getTexturedModelData);
    }
    private static <T extends Entity> EntityType<T> register(String s, EntityType<T> entityType) {
        return Registry.register(Registry.ENTITY_TYPE, Froststruck.MODID + ":" + s, entityType);
    }
}
