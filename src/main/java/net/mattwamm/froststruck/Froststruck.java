package net.mattwamm.froststruck;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.mattwamm.froststruck.callbacks.ServerTickSnow;
import net.mattwamm.froststruck.mixin.ThreadedAnvilChunkStorageInvoker;
import net.minecraft.block.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.Heightmap;
import net.minecraft.world.chunk.Chunk;

public class Froststruck implements ModInitializer {

    public static final String MODID = "froststuck";
    @Override
    public void onInitialize() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            ServerWorld world = server.getWorld(ServerWorld.OVERWORLD);
            if (MinecraftClient.getInstance().player == null)
                return;
            Iterable<ChunkHolder> chunkSet = ((ThreadedAnvilChunkStorageInvoker)world.getChunkManager().threadedAnvilChunkStorage).invokeEntryIterator();
            if(!world.isRaining()) return;
            for (ChunkHolder holder : chunkSet) {
                Chunk chunk = holder.getWorldChunk();
                if (chunk == null || !world.getChunkManager().isChunkLoaded(chunk.getPos().x, chunk.getPos().z)) {
                    continue;
                }
                int chunk_min_x = chunk.getPos().getStartX();
                int chunk_min_y = chunk.getPos().getStartZ();
                //If it can rain here, there is a 1/16 chance of trying to add snow
                if (world.random.nextInt(2) == 0) {
                    BlockPos pos1 = world.getTopPosition(Heightmap.Type.MOTION_BLOCKING,new BlockPos(chunk_min_x + world.random.nextInt(16), 1,chunk_min_y + world.random.nextInt(16)));
                    //Check if block at position is a snow layer block
                    Block topBlock = world.getBlockState(pos1).getBlock();
                    if(topBlock instanceof PlantBlock)
                    {
                        world.setBlockState(pos1,Blocks.AIR.getDefaultState());
                    }
                    if(topBlock instanceof SnowBlock) {
                        //Calculate mean surrounding block height
                        int height = world.getBlockState(pos1).get(SnowBlock.LAYERS);
                        float surroundings = 0;
                        for(Direction dir : Direction.values()) {
                            if (dir == Direction.DOWN || dir == Direction.UP) continue;
                            BlockState state = world.getBlockState(pos1.offset(dir));
                            if (state.getBlock() instanceof SnowBlock) {
                                surroundings += state.get(SnowBlock.LAYERS) / 4;
                            } else if (state.isFullCube(chunk, pos1.north())) {
                                surroundings += 2;
                            }
                        }
                        //Done calculating surroundings
                        if (surroundings >= height) {
                              float weight = (surroundings - height) / 2 + 0.05f;
                            if (world.random.nextFloat() <= weight) {
                                MinecraftClient.getInstance().player.sendMessage(Text.of("added a layer " + pos1 + height),true );
                                if(height >= 7) {
                                    world.setBlockState(pos1.offset(Direction.UP), Blocks.SNOW_BLOCK.getDefaultState());
                                    return;
                                }
                                    //Add layer!
                                world.setBlockState(pos1, Blocks.SNOW.getDefaultState().with(SnowBlock.LAYERS, height + 1));
                            }
                        }
                    }
                }
            }
        });

    }



}
