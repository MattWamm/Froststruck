package net.mattwamm.froststruck.render.models;

import com.google.common.collect.ImmutableList;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.mattwamm.froststruck.entities.SledEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.BoatEntityModel;
import net.minecraft.client.render.entity.model.CompositeEntityModel;

import net.minecraft.client.util.math.MatrixStack;

public class SledEntityModel extends CompositeEntityModel<SledEntity>{
    private final ModelPart LeftIron;
    private final ModelPart RightIron;
    private final ModelPart Wood;
    private final ImmutableList<ModelPart> parts;

    public SledEntityModel(ModelPart root) {
        this.LeftIron = root.getChild("LeftIron");
        this.RightIron = root.getChild("RightIron");
        this.Wood = root.getChild("Wood");
        parts = ImmutableList.of(this.Wood, this.LeftIron, this.RightIron);
    }
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();

        modelPartData.addChild("LeftIron", ModelPartBuilder.create().uv(31, 30).cuboid(6.0F, -1.0F, -9.0F, 1.0F, 1.0F, 26.0F)
                .uv(4, 14).cuboid(6.0F, -4.0F, -6.0F, 1.0F, 3.0F, 1.0F)
                .uv(8, 14).cuboid(6.0F, -4.0F, 3.0F, 1.0F, 3.0F, 1.0F)
                .uv(12, 14).cuboid(6.0F, -4.0F, 14.0F, 1.0F, 3.0F, 1.0F)
                .uv(12, 7).cuboid(6.0F, -2.0F, -11.0F, 1.0F, 1.0F, 4.0F)
                .uv(12, 0).cuboid(6.0F, -5.0F, -10.0F, 1.0F, 3.0F, 1.0F)
                .uv(0, 10).cuboid(6.0F, -3.0F, -12.0F, 1.0F, 1.0F, 2.0F)
                .uv(0, 3).cuboid(6.0F, -4.0F, -13.0F, 1.0F, 1.0F, 2.0F)
                .uv(18, 2).cuboid(6.0F, -5.0F, -13.0F, 1.0F, 1.0F, 1.0F) // different
                .uv(0, 30).cuboid(6.0F, -6.0F, -13.0F, 1.0F, 1.0F, 29.0F), ModelTransform.of(0.0F, 6.0F, 0F, 0.0f, -1.57079633f,0f));

        modelPartData.addChild("RightIron", ModelPartBuilder.create().uv(18, 0).cuboid(-7.0F, -5.0F, -13.0F, 1.0F, 1.0F, 1.0F)
                .uv(0, 0).cuboid(-7.0F, -4.0F, -13.0F, 1.0F, 1.0F, 2.0F)
                .uv(0, 7).cuboid(-7.0F, -3.0F, -12.0F, 1.0F, 1.0F, 2.0F)
                .uv(12, 0).cuboid(-7.0F, -2.0F, -11.0F, 1.0F, 1.0F, 4.0F)
                .uv(12, 7).cuboid(-7.0F, -5.0F, -10.0F, 1.0F, 3.0F, 1.0F)
                .uv(34, 57).cuboid(-7.0F, -1.0F, -9.0F, 1.0F, 1.0F, 26.0F)
                .uv(0, 14).cuboid(-7.0F, -4.0F, -6.0F, 1.0F, 3.0F, 1.0F)
                .uv(0, 18).cuboid(-7.0F, -4.0F, 3.0F, 1.0F, 3.0F, 1.0F)
                .uv(16, 14).cuboid(-7.0F, -4.0F, 14.0F, 1.0F, 3.0F, 1.0F)
                .uv(0, 0).cuboid(-7.0F, -6.0F, -13.0F, 1.0F, 1.0F, 29.0F), ModelTransform.of(0.0F, 6.0F, 0F, 0.0f, -1.57079633f,0f));

        modelPartData.addChild("Wood", ModelPartBuilder.create().uv(0, 5).cuboid(-8.0F, -5.0F, -9.0F, 3.0F, 1.0F, 6.0F)
                .uv(0, 3).cuboid(5.0F, -5.0F, -9.0F, 3.0F, 1.0F, 6.0F)                          //? / height in model /
                .uv(32, 8).cuboid(-8.0F, -5.0F, -3.0F, 16.0F, 1.0F, 19.0F), ModelTransform.of(0.0F, 6.0F, 0F, 0.0f, -1.57079633f,0f)); //turns 90 degrees in radians to point it straight

        return TexturedModelData.of(modelData, 128, 128);
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        LeftIron.render(matrixStack, buffer, packedLight, packedOverlay);
		RightIron.render(matrixStack, buffer, packedLight, packedOverlay);
		Wood.render(matrixStack, buffer, packedLight, packedOverlay);
    }

    public ImmutableList<ModelPart> getPartsImmutable() {
        return this.parts;
    }
    @Override
    public /* synthetic */ Iterable<ModelPart> getParts() {
        return this.getPartsImmutable();
    }

    @Override
    public void setAngles(SledEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {

    }

}