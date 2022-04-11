package net.mattwamm.froststruck;

import net.fabricmc.api.ModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class Froststruck implements ModInitializer {

    public static final String MODID = "froststuck";
    public final static MinecraftServer server = MinecraftClient.getInstance().getServer();
    public final static BlockPos spawn = server.getOverworld().getSpawnPos();
    @Override
    public void onInitialize() {
    }



}
