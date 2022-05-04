package net.mattwamm.froststruck;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.mattwamm.froststruck.registries.BlockRegistry;
import net.mattwamm.froststruck.registries.EventRegistry;
import net.mattwamm.froststruck.registries.ItemRegistry;
import net.minecraft.block.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Shearable;
import net.minecraft.fluid.LavaFluid;
import net.minecraft.item.HoeItem;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.command.WeatherCommand;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.Heightmap;
import net.minecraft.world.chunk.Chunk;
import org.apache.commons.lang3.tuple.Pair;

import java.util.function.Predicate;

public class Froststruck implements ModInitializer {

    public static final String MODID = "froststuck";

    @Override
    public void onInitialize() {
        EventRegistry.register();
        ItemRegistry.register();
        BlockRegistry.register();
    }


}
