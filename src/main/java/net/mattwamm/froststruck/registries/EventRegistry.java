package net.mattwamm.froststruck.registries;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.mattwamm.froststruck.components.EntityComponents;
import net.mattwamm.froststruck.items.Thermometer;
import net.mattwamm.froststruck.mixin.access.ThreadedAnvilChunkStorageInvoker;
import net.minecraft.block.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.Heightmap;
import net.minecraft.world.chunk.Chunk;

public class EventRegistry {

    private static final Identifier THERMOMETER_BACKGROUND = new Identifier("froststruck", "textures/hud/hud-thermo-background.png");
    private static final Identifier THERMOMETER_LINE = new Identifier("froststruck", "textures/hud/hud-thermo-line.png");
    public static boolean register() {
        ServerTickEvents.END_SERVER_TICK.register(snowTick);
        HudRenderCallback.EVENT.register(hudRender);
        return true;
    }
    static ServerTickEvents.EndTick snowTick = server -> {
        ServerWorld world = server.getWorld(ServerWorld.OVERWORLD);
        Iterable<ChunkHolder> chunkSet = ((ThreadedAnvilChunkStorageInvoker) world.getChunkManager().threadedAnvilChunkStorage).invokeEntryIterator();
        if (!world.isRaining()) return;
        for (ChunkHolder holder : chunkSet) {
            Chunk chunk = holder.getWorldChunk();
            if (chunk == null || !world.getChunkManager().isChunkLoaded(chunk.getPos().x, chunk.getPos().z)) {
                continue;
            }
            int chunk_min_x = chunk.getPos().getStartX();
            int chunk_min_y = chunk.getPos().getStartZ();
            //If it can rain here, there is a 1/16 chance of trying to add snow
            if (world.random.nextInt(11) == 0) {
                BlockPos pos1 = world.getTopPosition(Heightmap.Type.MOTION_BLOCKING, world.getRandomPosInChunk(chunk_min_x, 0, chunk_min_y, 15));
                //Check if block at position is a snow layer block
                Block topBlock = world.getBlockState(pos1).getBlock();
                if (topBlock instanceof PlantBlock) {
                    world.setBlockState(pos1, Blocks.SNOW.getDefaultState().with(SnowBlock.LAYERS, 1));
                }
                //TODO: add horizontal spread when block nearby is empty or 2~3 lower recursive so it fills spaces under trees and such

                if (topBlock instanceof SnowBlock) {
                    //Calculate mean surrounding block height
                    int height = world.getBlockState(pos1).get(SnowBlock.LAYERS);
                    float surroundings = 0;
                    for (Direction dir : Direction.values()) {
                        if (dir == Direction.UP) continue;
                        if (dir == Direction.DOWN) {
                            for (int i = 0; i < 7; i++) {
                                BlockState downCheck = world.getBlockState(pos1.offset(Direction.DOWN, i));
                                if (downCheck != Blocks.SNOW_BLOCK.getDefaultState()) {
                                    return;
                                }
                            }
                        }
                        BlockState state = world.getBlockState(pos1.offset(dir));
                        if (state.getBlock() instanceof SnowBlock) {
                            surroundings += state.get(SnowBlock.LAYERS) / 4.0;
                        } else if (state.isFullCube(chunk, pos1.north())) {
                            surroundings += 2;
                        }
                    }
                    //Done calculating surroundings
                    if (surroundings >= height || world.random.nextInt(5) == 0) {
                        float weight = (surroundings - height) / 2 + 0.05f;
                        if (world.random.nextFloat() <= weight) {
                            if (height >= 7) {
                                //TODO: add randomized powdered snow pools based on surrounding blocks
                                world.setBlockState(pos1, Blocks.SNOW_BLOCK.getDefaultState());
                                // MinecraftClient.getInstance().player.sendMessage(Text.of("added a layer " + pos1 + height),false);
                                return;
                            }
                            //Add layer!
                            world.setBlockState(pos1, Blocks.SNOW.getDefaultState().with(SnowBlock.LAYERS, height + 1));
                        }
                    }
                }
            }
        }
    };
    private static final HudRenderCallback hudRender = (matrixStack, tickDelta) -> {
        MinecraftClient minecraft = MinecraftClient.getInstance();
        TextRenderer renderer = minecraft.textRenderer;
        assert minecraft.player != null;
        //see if the thermometer is in the hotbar
        MatrixStack matrices = new MatrixStack();
        matrices.push();
        boolean thermometerEquipped = false;
        for (int i = 0; i < 9; i++) {
            if (minecraft.player.getInventory().getStack(i).getItem() instanceof Thermometer) {
                thermometerEquipped = true;
                break;
            }
        }
        if (thermometerEquipped) {
            //TODO:
            //Scale this properly

            int temp = EntityComponents.TEMP.get(minecraft.player).getValue();
            int scaledWidth = minecraft.getWindow().getScaledWidth();
            int scaledHeight = minecraft.getWindow().getScaledHeight();
            int thermometerY = 195;
            int thermometerX = 175;
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, THERMOMETER_BACKGROUND);
            DrawableHelper.drawTexture(matrixStack, (scaledWidth + thermometerX) / 2, (scaledHeight + thermometerY) / 2, 0, 0, 30, 30, 30, 30);
            RenderSystem.setShaderTexture(0, THERMOMETER_LINE);
            DrawableHelper.drawTexture(matrixStack, (scaledWidth + thermometerX) / 2, (scaledHeight + thermometerY + 9 - temp) / 2, 0, 0, 30, 12 + temp, 30, 30);
            renderer.draw(matrixStack, "" + temp, 0, 100, 0xff0000);
        }
    };
}

