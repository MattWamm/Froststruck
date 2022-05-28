package net.mattwamm.froststruck;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.mattwamm.froststruck.registries.*;
import net.mattwamm.froststruck.weather.Blizzard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

import javax.security.auth.callback.Callback;

public class Froststruck implements ModInitializer {

    public static final String MODID = "froststruck";

    @Override
    public void onInitialize() {
        Blizzard.blizzardInit();
        EventRegistry.register();
        ItemRegistry.register();
        BlockRegistry.register();
        CommandRegistry.Register();
    }

    public static Identifier id(String path) {
        return new Identifier(MODID, path);
    }

}
