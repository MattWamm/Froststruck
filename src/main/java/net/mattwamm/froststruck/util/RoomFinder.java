package net.mattwamm.froststruck.util;

import net.mattwamm.froststruck.Blocks.AbstractHeatBlock;
import net.mattwamm.froststruck.Blocks.HeatBlock;
import net.mattwamm.froststruck.Room;
import net.mattwamm.froststruck.components.RoomComponent;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;

public final class RoomFinder {
    static int ROOMCHECKSIZE = 100;
    public static Room CreateRoom(RoomComponent roomComponent, World world)
    {
        //Setup starting variables
        BlockPos roomPos = roomComponent.provider.getPos();
        //Setup variables for floodfill
        Deque<BlockPos> blockQueue = new ArrayDeque<>();
        blockQueue.add(roomPos);
        ArrayList<BlockPos> airBlocks = new ArrayList<>();
        ArrayList<BlockPos> walls = new ArrayList<>();
        ArrayList<BlockPos> blocksInside = new ArrayList<>();
        ArrayList<BlockPos> heatBlocks = new ArrayList<>();
        int[][][] bounds = new int[ROOMCHECKSIZE][ROOMCHECKSIZE][ROOMCHECKSIZE/32];
        Vec3i lowerBounds = roomPos;
        Vec3i upperBounds = roomPos;
        while (!blockQueue.isEmpty())
        {
            BlockPos curPos = blockQueue.remove();
            BlockPos[] directions = { curPos.down(), curPos.up(), curPos.north(), curPos.east(), curPos.south(),
                    curPos.west() };
            for(BlockPos blockPos : directions)
            {
                BlockPos checkingPos = blockPos;
                //Checked check
                try{
                    if (Boolean.TRUE.equals(hasBeenChecked(bounds,roomPos, checkingPos, ROOMCHECKSIZE, true))) {
                        continue;
                    }
                }
                catch (Exception e) {
                    MinecraftClient.getInstance().player.sendMessage(new LiteralText(e.toString()), false);
                    continue;
                }

                //Lowerbounds
                if(curPos.getY() < lowerBounds.getY())
                    lowerBounds = new Vec3i(lowerBounds.getX(), curPos.getY(), lowerBounds.getZ());
                if(curPos.getX() < lowerBounds.getX())
                    lowerBounds = new Vec3i(curPos.getX(), lowerBounds.getY(), lowerBounds.getZ());
                if(curPos.getZ() < lowerBounds.getZ())
                    lowerBounds = new Vec3i(lowerBounds.getX(), lowerBounds.getY(), curPos.getZ());
                //Upperbounds
                if(curPos.getY() > upperBounds.getY())
                    upperBounds = new Vec3i(upperBounds.getX(), curPos.getY(), upperBounds.getZ());
                if(curPos.getX() > upperBounds.getX())
                    upperBounds = new Vec3i(curPos.getX(), upperBounds.getY(), upperBounds.getZ());
                if(curPos.getZ() > upperBounds.getZ())
                    upperBounds = new Vec3i(upperBounds.getX(), upperBounds.getY(), curPos.getZ());
                //Check block
                double distance = checkingPos.getManhattanDistance(roomPos);
                if (distance >= ROOMCHECKSIZE - 1) {
                    return null;
                }
                BlockState state = world.getBlockState(checkingPos);

                if(state.isAir())
                {
                    airBlocks.add(checkingPos);
                    if(Boolean.TRUE.equals(canSeeSky(world, checkingPos)))
                    {
                        MinecraftClient.getInstance().player.sendMessage(new LiteralText(
                                        "Open sky at: " + checkingPos.toShortString() + " and a distance of " + distance),
                                true);
                        return null;
                    }
                    if (curPos.getY() > checkingPos.getY())
                        blockQueue.addLast(checkingPos);
                    else
                        blockQueue.addFirst(checkingPos);

                }
                else if (state.getBlock() instanceof HeatBlock) {
                    heatBlocks.add(checkingPos);
                } else {
                    blocksInside.add(checkingPos);
                }
            }
        }
        for (BlockPos blockPos : blocksInside) {
            for (Direction dir : Direction.values()) {
                BlockPos offset = blockPos.offset(dir);
                BlockState state = world.getBlockState(offset);
                if (state.isAir()) {
                    if (Boolean.FALSE.equals(hasBeenChecked(bounds, roomPos, offset, ROOMCHECKSIZE, false))) {
                        walls.add(blockPos);
                        break;
                    }
                }
            }
        }
        Room room = new Room(roomPos,airBlocks,walls,blocksInside,heatBlocks,lowerBounds,upperBounds);
        return room;
    }

    public static Boolean canSeeSky(World world, BlockPos pos) {
        return world.getTopPosition(Heightmap.Type.WORLD_SURFACE, pos).getY() <= pos.getY();
    }

    public static Boolean hasBeenChecked(int[][][] bounds,BlockPos middle, BlockPos check, int size, Boolean setTrue)
            throws IndexOutOfBoundsException {
        BlockPos subbed = check.subtract(middle).add((size), (size), (size));
        if (subbed.getX() < 0 || subbed.getY() < 0 || subbed.getZ() < 0) {
            throw new IndexOutOfBoundsException(
                    subbed.toShortString() + " Check: (" + check.toShortString() + "), Middle: "
                            + middle.toShortString() + "  Original number: " + check.subtract(middle).toShortString()
                            + " Size " + ((size / 2) + 2));
        }
        Boolean checked = CheckPos(bounds,subbed.getX(),subbed.getY(),subbed.getZ());
        if (setTrue)
            SetPos(bounds,subbed.getX(),subbed.getY(),subbed.getZ());
        return checked == Boolean.TRUE;
    }

    public static final boolean CheckPos(int[][][] bounds, int x, int y, int z)
    {
        return (bounds[x][y][z/32] & 1 << (z%32)) != 0;
    }
    public static final void SetPos(int[][][] bounds, int x, int y, int z)
    {
        bounds[x][y][z/32] |= 1 << z%32;
    }
    public static final void ClearPos(int[][][] bounds, int x, int y, int z)
    {
        bounds[x][y][z/32] &= (1 << (z %32) ^ -1);
    }
}
