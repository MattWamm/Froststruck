package net.fabricmc.roomdetector.Blocks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome.TemperatureModifier;

public class Room {

    private static HashMap<Material, Float> thermalConductivity = new HashMap<>() {
        {
            put(Material.WOOD, 0.17f);
            put(Material.STONE, 1.7f);
            put(Material.GLASS, 1.05f);
            put(Material.WOOL, 0.039f);
        }
    };

    public static void addRoom(BlockPos boundBlock, Room room)// Boom boom boom boom
    {
        roomsActive.put(boundBlock, room);
    }

    public static void removeRoom(BlockPos boundBlock) {
        roomsActive.remove(boundBlock);
    }

    public static Boolean hasRoomBound(BlockPos state) {
        return roomsActive.containsKey(state);
    }

    public static Room getRoom(BlockPos state) {
        if (roomsActive.containsKey(state)) {
            return roomsActive.get(state);
        }
        return null;
    }

    private static HashMap<BlockPos, Room> roomsActive = new HashMap<>();

    static float timePerTick = (1000f / 60);
    float realTime = 0;

    World clientWorld;
    World serverWorld;
    ArrayList<BlockPos> airblocks;
    ArrayList<BlockPos> blocksInside;
    ArrayList<BlockPos> walls;
    private Float heatloss;
    private Float roomConductivity;
    int roomSize;
    float airMass;
    float heatCapacity;
    float currentRoomHeat;
    ArrayList<BlockPos> heatBlocks;

    public Room(List<BlockPos> initialAirBlocks, List<BlockPos> initialBlocksInside, List<BlockPos> initialHeatBlocks,
            List<BlockPos> initialWalls, PlayerEntity creator, World clientWorld,
            World serverWorld, float currentRoomTemp, BlockPos boundBlock) {
        airblocks = new ArrayList<>(initialAirBlocks);
        roomSize = airblocks.size();
        airMass = (float) roomSize * 1.205f;
        heatCapacity = airMass * 1.005f;
        currentRoomHeat = currentRoomTemp;
        blocksInside = new ArrayList<>(initialBlocksInside);
        walls = new ArrayList<>(initialWalls);
        this.serverWorld = serverWorld;
        this.clientWorld = clientWorld;
        roomConductivity = calculateConductivity(creator);
        this.heatBlocks = new ArrayList<>(initialHeatBlocks);
        creator.sendMessage(
                new LiteralText("Conductivity: " + roomConductivity + " | Heatloss: " + calculateHeatLoss(1, 25, -40)),
                false);
        addRoom(boundBlock, this);
    }

    public static void UpdateRooms() {
        roomsActive.forEach((block, value) -> {
            value.UpdateRoom();
        });
    }

    public void UpdateRoom() {
        realTime += 1;
        if (realTime > timePerTick) {
            realTime = realTime % timePerTick;
            float addedHeat = 0;
            Iterator<BlockPos> it = heatBlocks.iterator();
            while(it.hasNext()) {
                BlockPos heatBlock = it.next();
                BlockState newState = clientWorld.getBlockState(heatBlock);
                if (newState.isAir()) {
                    it.remove();
                    continue;
                }
                    addedHeat += ((AbstractHeatBlock) clientWorld.getBlockState(heatBlock).getBlock()).GetHeatOutput()
                            / timePerTick;
            }
            currentRoomHeat += (addedHeat / heatCapacity)
                    - ((calculateHeatLoss(1, currentRoomHeat, -40) / roomConductivity) / timePerTick);
        }
    }

    public float GetRoomTemp() {
        return currentRoomHeat;
    }

    private float calculateConductivity(PlayerEntity playerEntity) {
        float allCond = 0f;
        for (BlockPos blockState : walls) {
            try {
                allCond += thermalConductivity.get(playerEntity.world.getBlockState(blockState).getMaterial());
            } catch (Exception e) {

                playerEntity.sendMessage(
                        new LiteralText(
                                "Not registered  " + playerEntity.world.getBlockState(blockState).getBlock().getName()),
                        false);
            }
        }
        return allCond;
    }

    private float calculateHeatLoss(float difficultyModifier, float insideTemp, float outsideTemp) {
        if (insideTemp > outsideTemp)
            return (roomConductivity * (insideTemp - outsideTemp)) / difficultyModifier;
        else
            return (roomConductivity * (outsideTemp - insideTemp)) / difficultyModifier;
    }

}
