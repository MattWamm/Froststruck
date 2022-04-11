package net.mattwamm.froststruck.mixin;


import net.mattwamm.froststruck.components.Components;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static java.lang.Math.*;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity{


    private Double ambientTemperature;
    private final BlockPos spawn = MinecraftClient.getInstance().getServer().getOverworld().getSpawnPos();
    private final MinecraftServer server = MinecraftClient.getInstance().getServer();
    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }


    private void ChangeTemp(PlayerEntity player)
    {
//        Double distance = floor(spawn.getSquaredDistance(player.getPos()));
//        ambientTemperature = min(0, -(floor(distance / 5000)));
//        //server.getPlayerManager().broadcast(Text.of(ambientTemperature.toString()), MessageType.CHAT,player.getUuid());
//        Components.TEMP.get(player).setValue(ambientTemperature.intValue());
    }

    @Inject(method = "tick", at = @At("RETURN"))
    public void onTick(CallbackInfo ci)
    {

        PlayerEntity player = ((PlayerEntity)(Object)this);
        Components.TEMP.get(player);

//        ChangeTemp(player);
    }

}
