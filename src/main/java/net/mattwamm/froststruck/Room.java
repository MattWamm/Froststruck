package net.mattwamm.froststruck;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class Room {

    ArrayList<BlockPos> airBlocks;
    ArrayList<BlockPos> walls;
    ArrayList<BlockPos> blocksInside;
    ArrayList<BlockPos> heatBlocks;
    int[][][] bounds;

    Vec3i lowerBounds;
    Vec3i upperBounds;

    public Room(BlockPos roomMiddle,ArrayList<BlockPos> airBlocks, ArrayList<BlockPos> walls, ArrayList<BlockPos> blocksInside,ArrayList<BlockPos> heatBlocks, Vec3i lowerBounds, Vec3i upperBounds)
    {
        this.walls = walls;
        this.blocksInside = blocksInside;
        this.heatBlocks = heatBlocks;
        this.airBlocks = airBlocks;
        this.lowerBounds = lowerBounds;
        this.upperBounds = upperBounds;
    }
}
