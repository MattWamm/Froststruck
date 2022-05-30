package net.mattwamm.froststruck.client.render.renderers;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.mattwamm.froststruck.Froststruck;
import net.mattwamm.froststruck.client.FroststruckClient;
import net.mattwamm.froststruck.entities.SledEntity;
import net.mattwamm.froststruck.client.render.models.SledEntityModel;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;

import java.util.Map;
import java.util.stream.Stream;

@Environment(EnvType.CLIENT)
public class SledEntityRenderer extends EntityRenderer<SledEntity> {
    private final Map<SledEntity.Type, Pair<Identifier, SledEntityModel>> texturesAndModels;
    public SledEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
        this.shadowRadius = 0.8f;
        this.texturesAndModels = Stream.of(SledEntity.Type.values()).collect(ImmutableMap.toImmutableMap(type -> type, sledType -> Pair.of(new Identifier(Froststruck.MODID,"textures/entity/sled/" + sledType.getName() + ".png"), new SledEntityModel(ctx.getPart(FroststruckClient.SLED_MODEL_LAYER)))));
    }

    @Override
    public void render(SledEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        matrices.push();
        matrices.translate(0.0, 0.375, 0.0);
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180.0f - yaw));
        float h = (float) entity.getDamageWobbleTicks() - tickDelta;
        float j = entity.getDamageWobbleStrength() - tickDelta;
        if (j < 0.0f) {
            j = 0.0f;
        }
        if (h > 0.0f) {
            matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(MathHelper.sin(h) * h * j / 10.0f * (float) entity.getDamageWobbleSide()));
        }
        matrices.scale(-1.0f, -1.0f, 1.0f);
        Pair<Identifier, SledEntityModel> pair = this.texturesAndModels.get(entity.getSledType());
        Identifier identifier = pair.getFirst();
        SledEntityModel sledEntityModel = pair.getSecond();
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(90.0f));
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(sledEntityModel.getLayer(identifier));
        sledEntityModel.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, 1.0f, 1.0f, 1.0f, 1.0f);
        matrices.pop();
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
    }
    @Override
    public Identifier getTexture(SledEntity sledEntity) {
        return this.texturesAndModels.get(sledEntity.getSledType()).getFirst();
    }




}
