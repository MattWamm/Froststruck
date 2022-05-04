package net.mattwamm.froststruck.blocks;


import net.mattwamm.froststruck.registries.BlockRegistry;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class HeatSourceBlockEntity extends BlockEntity {

    private int heatInWatt;

    public HeatSourceBlockEntity(BlockPos pos, BlockState state) {
        super(BlockRegistry.HEAT_SOURCE, pos, state);
    }

    @Override
    public void writeNbt(NbtCompound tag) {
        // Save the current value of the number to the tag
        tag.putInt("heatInWatt", heatInWatt);

        super.writeNbt(tag);
    }
    @Override
    public void readNbt(NbtCompound tag){
        super.readNbt(tag);
        heatInWatt = tag.getInt("heatInWatt");
    }
    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

    public static void tick(World world, BlockPos pos, BlockState state, HeatSourceBlockEntity be) {
        //add ticking if needed
    }


}
