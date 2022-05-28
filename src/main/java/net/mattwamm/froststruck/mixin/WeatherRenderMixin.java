package net.mattwamm.froststruck.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.mattwamm.froststruck.Froststruck;
import net.mattwamm.froststruck.weather.Blizzard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

import static net.minecraft.client.render.WorldRenderer.getLightmapCoordinates;

@Mixin(WorldRenderer.class)
public abstract class WeatherRenderMixin {

    private static int ticksActive;
    @Shadow
    private int ticks;

    @Inject(method = "renderWeather", at = @At("HEAD"))
    private void renderWeatherInject(LightmapTextureManager manager, float f, double d, double e, double g, CallbackInfo ci) {
        if (Blizzard.blizzardActive) {
            if (ticksActive >= 2000) {
                Blizzard.setBlizzardGradient(0);
                Blizzard.blizzardActive = false;
                ticksActive = 0;
                return;
            }
            ++ticksActive;
            Blizzard.renderBlizzard(manager, f, d , e, g, ticks);
            return;
        }

    }
}
