package net.mattwamm.froststruck.mixin;


import net.mattwamm.froststruck.components.BlockComponents;
import net.mattwamm.froststruck.components.RoomComponent;
import net.mattwamm.froststruck.util.IsolatedRoom;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
public class CampFireMixin {

    @Inject(method = "onPlaced", at = @At("RETURN"))
    private void HeatPlacedInject(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack, CallbackInfo ci)
    {
        if(state.getBlock() instanceof CampfireBlock)
        {
            IsolatedRoom room = BlockComponents.ROOM.get(state.getBlock()).getRoom();
            if (!world.isClient) {
                int maxBlockDistance = 40, blockDistance = 0;
                Boolean sawSky = false;

                if(placer instanceof PlayerEntity)
                    ((PlayerEntity) placer).sendMessage(new LiteralText("Is inside " + room.CreateRoom(world, pos, (PlayerEntity)placer).toString()), false);
                room.addSource(state);
            }
        }
    }


}
