package net.mattwamm.froststruck;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Position;
import net.minecraft.world.World;

import java.util.List;
import java.util.PriorityQueue;

public class Room {

    private final World world;

    public Room(BlockEntity source)
    {
        world = source.getWorld();
        BlockPos pos = source.getPos();
        Block block;





    }

    private void checkNeighbours(BlockPos pos)
    {
        PriorityQueue<BlockState> queue;
        for (Direction value : Direction.values()) {
            BlockState state = world.getBlockState(pos.offset(value));
            if(state.isAir())
            {

            }
            else if(!state.isAir());
            {

            }
        }
    }

    private boolean checkSky(BlockState state)
    {

        return true;
    }

}
