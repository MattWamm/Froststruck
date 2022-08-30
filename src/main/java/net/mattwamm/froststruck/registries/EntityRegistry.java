package net.mattwamm.froststruck.registries;

import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.mattwamm.froststruck.Froststruck;
import net.mattwamm.froststruck.client.render.models.SledEntityModel;
import net.mattwamm.froststruck.client.render.renderers.SledEntityRenderer;
import net.mattwamm.froststruck.entities.SledEntity;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;


public class EntityRegistry {

    public static final EntityType<SledEntity> SLED = Registry.register(
            Registry.ENTITY_TYPE,
            new Identifier(Froststruck.MODID,"sled"),
            FabricEntityTypeBuilder.<SledEntity>create(SpawnGroup.MISC, SledEntity::new).dimensions(EntityDimensions.fixed(0.9f,0.5f)).build()
    );
    public static void Register() {
        for (SledEntity.Type type : SledEntity.Type.values()) {
            EntityModelLayerRegistry.registerModelLayer(createSled(type), SledEntityModel::getTexturedModelData);
        }
        EntityRendererRegistry.register(SLED, SledEntityRenderer::new);
    }

    private static <T extends Entity> EntityType<T> register(String entityName, EntityType<T> entityType) {
        return Registry.register(Registry.ENTITY_TYPE, Froststruck.MODID + ":" + entityName, entityType);
    }

    public static EntityModelLayer createSled(SledEntity.Type type) {
        return new EntityModelLayer(new Identifier(Froststruck.MODID,"sled/" + type.getName()), "main");
    }

}
