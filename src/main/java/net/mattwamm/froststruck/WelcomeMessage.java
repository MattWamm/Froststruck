package net.mattwamm.froststruck;


import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.World;

public class WelcomeMessage {

    void sendMessage()
    {
        MinecraftClient.getInstance().player.sendChatMessage("ass");
    }


}