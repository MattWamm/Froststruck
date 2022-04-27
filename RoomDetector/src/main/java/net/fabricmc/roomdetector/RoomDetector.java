package net.fabricmc.roomdetector;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.C2SPlayChannelEvents.Register;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.roomdetector.Blocks.ConstantHeatBlock;
import net.fabricmc.roomdetector.Blocks.Room;
import net.fabricmc.roomdetector.Blocks.RoomSizeCounter;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.item.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RoomDetector implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger("modid");
	public static final Block ROOMSIZECOUNTER = new RoomSizeCounter(FabricBlockSettings.of(Material.STONE).strength(1));
	public static final Block CONTSTANTHEATBLOCK = new ConstantHeatBlock(FabricBlockSettings.of(Material.STONE).strength(1));
	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		Registry.register(Registry.BLOCK, new Identifier("roomdetector", "roomsizecounter"), ROOMSIZECOUNTER);
		Registry.register(Registry.ITEM, new Identifier("roomdetector", "roomsizecounter"), new BlockItem(ROOMSIZECOUNTER, new FabricItemSettings().group(ItemGroup.MISC)));
		Registry.register(Registry.BLOCK, new Identifier("roomdetector", "heatsource"), CONTSTANTHEATBLOCK);
		Registry.register(Registry.ITEM, new Identifier("roomdetectzor", "heatsource"), new BlockItem(CONTSTANTHEATBLOCK, new FabricItemSettings().group(ItemGroup.MISC)));
		ServerTickEvents.START_WORLD_TICK.register((world) -> {
           Room.UpdateRooms();
        });
		LOGGER.info("Hello Fabric world!");
	}
}
