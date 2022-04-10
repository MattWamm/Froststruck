package net.mattwamm.froststruck.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.MessageType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityNbtMixin {

        private final MinecraftServer server = MinecraftClient.getInstance().getServer();
        @Inject(method = "writeNbt", at = @At("RETURN"), cancellable = true )
        public void WriteExtraNbtData(NbtCompound nbt, CallbackInfoReturnable<NbtCompound> cir)
        {
            if(nbt.getInt("temperature") != 0) {
                server.getPlayerManager().broadcast(Text.of(nbt.getInt("temperature") + ""), MessageType.CHAT, nbt.getUuid("UUID"));
            }
            nbt.putInt("temperature", 15);
            cir.setReturnValue(nbt);
        }
}
