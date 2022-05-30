package net.mattwamm.froststruck.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.mattwamm.froststruck.Froststruck;
import net.mattwamm.froststruck.registries.EntityRegistry;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class FroststruckClient implements ClientModInitializer {

    public static final EntityModelLayer SLED_MODEL_LAYER = new EntityModelLayer(new Identifier(Froststruck.MODID,"sled/oak"), "main");

    @Override
    public void onInitializeClient() {
        EntityRegistry.Register();
    }
}
