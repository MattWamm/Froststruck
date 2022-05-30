package net.mattwamm.froststruck.registries;

import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.mattwamm.froststruck.Froststruck;
import net.mattwamm.froststruck.client.FroststruckClient;
import net.mattwamm.froststruck.entities.SledEntity;
import net.mattwamm.froststruck.client.render.models.SledEntityModel;
import net.mattwamm.froststruck.client.render.renderers.SledEntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.registry.Registry;


public class EntityRegistry {
    public static  EntityType<SledEntity> SLED;
    public static void Register(){
        //register the model layers.
        EntityModelLayerRegistry.registerModelLayer(FroststruckClient.SLED_MODEL_LAYER, SledEntityModel::getTexturedModelData);

        //register the actual entity and save it for later.
        SLED = register("oak_sled",FabricEntityTypeBuilder.<SledEntity>create(SpawnGroup.MISC, SledEntity::new).dimensions(EntityDimensions.fixed(0.90f, 0.50f)).build());

        //register the renderer for said entity.
        EntityRendererRegistry.register(SLED, SledEntityRenderer::new);
    }

    private static <T extends Entity> EntityType<T> register(String entityName, EntityType<T> entityType) {
        return Registry.register(Registry.ENTITY_TYPE, Froststruck.MODID + ":" + entityName, entityType);
    }
}
