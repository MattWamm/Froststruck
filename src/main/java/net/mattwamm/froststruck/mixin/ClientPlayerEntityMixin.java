package net.mattwamm.froststruck.mixin;

import com.mojang.authlib.GameProfile;
import net.mattwamm.froststruck.entities.SledEntity;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.vehicle.BoatEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin extends AbstractClientPlayerEntity {
    @Shadow
    public Input input;
    @Shadow
    private boolean riding;

    public ClientPlayerEntityMixin(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    @Inject(method = "tickRiding", at = @At("TAIL"))
    public void tickRiding(CallbackInfo ci)
    {
        if (this.getVehicle() instanceof SledEntity) {
            SledEntity sledEntity = (SledEntity)this.getVehicle();
            sledEntity.setInputs(this.input.pressingLeft, this.input.pressingRight,this.input.pressingForward, this.input.pressingBack);
            this.riding |= this.input.pressingLeft || this.input.pressingRight || this.input.pressingForward || this.input.pressingBack;
        }
    }

}
