package net.mattwamm.froststruck.util;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import net.minecraft.block.*;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.util.*;
import org.jetbrains.annotations.Nullable;

public class IsolatedRoom {


    private int HeatEfficiencyPercentage = 100;
    ArrayList<BlockState> blocksInside;
    ArrayList<BlockState> sources;
    ArrayList<BlockPos> airblocks;

    public IsolatedRoom() {
        blocksInside = new ArrayList<BlockState>();
        airblocks = new ArrayList<BlockPos>();
    }

    public void addSource(BlockState state) {
        sources.add(state);
    }

    public void removeSource(BlockState state) {
        sources.remove(state);
    }

    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity player, ItemStack itemStack) {
        if (!world.isClient) {
            int maxBlockDistance = 40, blockDistance = 0;
            Boolean sawSky = false;

            if (player instanceof PlayerEntity)
                ((PlayerEntity) player).sendMessage(new LiteralText("Is inside " + CreateRoom(world, pos, (PlayerEntity) player).toString()), false);
            sources.clear();
            sources.add(state);
        }
    }

    public Boolean CreateRoom(World world, BlockPos middle, PlayerEntity player) {
        Queue<BlockPos> blockQueue = new LinkedList<>();
        airblocks.clear();
        blocksInside.clear();
        blockQueue.add(middle);
        player.sendMessage(new LiteralText("Starting check at " + middle.toShortString()), false);

        //need to add calculating Efficiency here adding appropriate values

        while (!blockQueue.isEmpty()) {
            BlockPos curPos = blockQueue.remove();
            if (airblocks.stream().noneMatch(o -> o.equals(curPos))) {
                airblocks.add(curPos);
                for (Direction value : Direction.values()) {
                    BlockPos checkingPos = curPos.offset(value);
                    if (blockQueue.contains(checkingPos))
                        continue;
                    BlockState curEntity = world.getBlockState(checkingPos);
                    player.sendMessage(new LiteralText(checkingPos.toShortString() + " : can see sky " + canSeeSky(world, checkingPos).toString()), false);
                    double distance = checkingPos.getManhattanDistance(middle);
                    if (distance > 40)
                        return false;
                    if (curEntity.isAir()) {
                        if (Boolean.TRUE.equals(canSeeSky(world, checkingPos)))
                            return false;
                        blockQueue.add(checkingPos);
                    } else {
                        blocksInside.add(curEntity);
                    }
                }
            }
        }
        return true;
    }

    public Boolean canSeeSky(World world, BlockPos pos) {
        return world.getTopPosition(Heightmap.Type.WORLD_SURFACE, pos).getY() <= pos.getY();
    }
}
