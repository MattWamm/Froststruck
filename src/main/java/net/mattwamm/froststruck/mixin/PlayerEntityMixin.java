package net.mattwamm.froststruck.mixin;


import net.mattwamm.froststruck.Froststruck;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.datafixer.fix.BlockEntityKeepPacked;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.MessageType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
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
        Double distance = floor(spawn.getSquaredDistance(player.getPos()));
        ambientTemperature = min(0, -(floor(distance / 5000)));
        //server.getPlayerManager().broadcast(Text.of(ambientTemperature.toString()), MessageType.CHAT,player.getUuid());

    }

    @Inject(method = "tick", at = @At("RETURN"))
    public void onTick(CallbackInfo ci)
    {
        PlayerEntity player = ((PlayerEntity)(Object)this);
        ChangeTemp(player);
    }

}
