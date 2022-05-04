package net.mattwamm.froststruck.weather;


import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.rendering.v1.DimensionRenderingRegistry;
import net.minecraft.client.particle.RainSplashParticle;
import net.minecraft.entity.vehicle.CommandBlockMinecartEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.chunk.Chunk;

public abstract class WeatherEvent {

    public WeatherEvent(){

    }

    public abstract void worldTick(ServerWorld world, int tickSpeed, long worldTime);
    public abstract void chunkTick(Chunk chunk, ServerWorld world);

}
