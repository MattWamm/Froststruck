package net.mattwamm.froststruck.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.mattwamm.froststruck.entities.SledEntity;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.network.packet.s2c.play.EntityAttachS2CPacket;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {

    @Shadow
    private ClientWorld world;

    @Inject(method = "onEntityAttach", at= @At(value = "TAIL"))
    public void onEntityAttach(EntityAttachS2CPacket packet, CallbackInfo ci) {
        Entity entity = this.world.getEntityById(packet.getAttachedEntityId());
        if (entity instanceof SledEntity sled) {
            sled.setHoldingEntityId(packet.getHoldingEntityId());
        }
    }

}
