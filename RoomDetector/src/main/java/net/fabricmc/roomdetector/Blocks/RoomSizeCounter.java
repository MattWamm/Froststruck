package net.fabricmc.roomdetector.Blocks;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import com.ibm.icu.impl.UResource.Array;
import com.ibm.icu.impl.duration.TimeUnit;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.*;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.EmptyFluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.text.LiteralText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.util.*;

public class RoomSizeCounter extends Block {

    ArrayList<BlockPos> blocksInside;
    ArrayList<BlockPos> walls;
    ArrayList<BlockPos> airblocks;
    ArrayList<BlockPos> heatBlocks;
    Boolean[][][] isChecked;

    public RoomSizeCounter(Settings settings) {
        super(settings);
        blocksInside = new ArrayList<BlockPos>();
        airblocks = new ArrayList<BlockPos>();
        heatBlocks = new ArrayList<>();
        walls = new ArrayList<>();
    }

    @Override
    public void onBroken(WorldAccess world, BlockPos pos, BlockState state) {
        super.onBroken(world, pos, state);
        Room.removeRoom(pos);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand,
            BlockHitResult hit) {
        if (world.isClient) {
            if (!Room.hasRoomBound(pos)) {
                int maxBlockDistance = 40, blockDistance = 0;
                Boolean sawSky = false;
                Boolean roomCreated = CreateRoom(world, pos, player, 300);
                player.sendMessage(new LiteralText("Is inside " + roomCreated.toString()), false);
                if (Boolean.TRUE.equals(roomCreated))
                    new Room(airblocks, blocksInside, heatBlocks, walls, player, world, null, 0, pos);
                return ActionResult.SUCCESS;
            } else {
                player.sendMessage(new LiteralText("Current temp: " + Room.getRoom(pos).GetRoomTemp()), false);
            }
        }

        return ActionResult.SUCCESS;
    }

    public Boolean CreateRoom(World world, BlockPos middle, PlayerEntity player, int maxSizeCubed) {
        // TODO Auto-generated method stub
        int checked = 0;
        Deque<BlockPos> blockQueue = new ArrayDeque<>();
        airblocks.clear();
        blocksInside.clear();
        heatBlocks.clear();
        walls.clear();
        blockQueue.add(middle);
        isChecked = new Boolean[(maxSizeCubed * 2) + 2][maxSizeCubed * 2][maxSizeCubed * 2];
        player.sendMessage(new LiteralText("Starting check at " + middle.toShortString()), false);
        int topQueuePos = 0;
        while (!blockQueue.isEmpty()) {
            BlockPos curPos = blockQueue.remove();
            BlockPos[] directions = { curPos.down(), curPos.up(), curPos.north(), curPos.east(), curPos.south(),
                    curPos.west() };

            for (BlockPos blockPos : directions) {
                BlockPos checkingPos = blockPos;
                try {
                    if (Boolean.TRUE.equals(hasBeenChecked(middle, checkingPos, maxSizeCubed, true))) {
                        continue;
                    }
                } catch (Exception e) {
                    player.sendMessage(new LiteralText(e.toString()), false);
                    continue;
                }
                BlockState curEntity = world.getBlockState(checkingPos);
                double distance = checkingPos.getManhattanDistance(middle);
                if (distance >= maxSizeCubed - 1) {
                    player.sendMessage(new LiteralText("Blocks checked: " + checked), false);
                    isChecked = null;
                    return false;
                }
                if (curEntity.isAir()) {
                    airblocks.add(checkingPos);
                    if (Boolean.TRUE.equals(canSeeSky(world, checkingPos))) {
                        player.sendMessage(new LiteralText(
                                "Open sky at: " + checkingPos.toShortString() + " and a distance of " + distance
                                        + ", Blocks checked: " + checked),
                                false);
                        isChecked = null;
                        return false;
                    }
                    if (curPos.getY() > checkingPos.getY())
                        blockQueue.addLast(checkingPos);
                    else
                        blockQueue.addFirst(checkingPos);
                } else if (curEntity.getBlock() instanceof AbstractHeatBlock) {
                    heatBlocks.add(checkingPos);
                } else {
                    blocksInside.add(checkingPos);
                }
                checked++;
            }
        }
        for (BlockPos blockPos : blocksInside) {
            for (Direction dir : Direction.values()) {
                BlockPos offset = blockPos.offset(dir);
                BlockState state = world.getBlockState(offset);
                if(state.isAir())
                {
                   if(Boolean.FALSE.equals(hasBeenChecked(middle, offset, maxSizeCubed, false)))
                   {
                       walls.add(blockPos);
                       break;
                   }
                }
            }
        }
        player.sendMessage(new LiteralText("Blocks checked: " + checked), false);
        isChecked = null;
        return true;
    }

    public Boolean hasBeenChecked(BlockPos middle, BlockPos check, int size, Boolean setTrue)
            throws IndexOutOfBoundsException {
        BlockPos subbed = check.subtract(middle).add((size), (size), (size));
        if (subbed.getX() < 0 || subbed.getY() < 0 || subbed.getZ() < 0) {
            throw new IndexOutOfBoundsException(
                    subbed.toShortString() + " Check: (" + check.toShortString() + "), Middle: "
                            + middle.toShortString() + "  Original number: " + check.subtract(middle).toShortString()
                            + " Size " + ((size / 2) + 2));
        }
        Boolean checked = isChecked[subbed.getX()][subbed.getY()][subbed.getZ()];
        if (setTrue)
            isChecked[subbed.getX()][subbed.getY()][subbed.getZ()] = Boolean.TRUE;
        return checked == Boolean.TRUE;
    }

    public Boolean canSeeSky(World world, BlockPos pos) {
        return world.getTopPosition(Heightmap.Type.WORLD_SURFACE, pos).getY() <= pos.getY();
    }
}
