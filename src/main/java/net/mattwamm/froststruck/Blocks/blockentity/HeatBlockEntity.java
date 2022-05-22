package net.mattwamm.froststruck.Blocks.blockentity;

import dev.onyxstudios.cca.api.v3.component.tick.ServerTickingComponent;
import net.mattwamm.froststruck.components.IntComponent;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;


public class HeatBlockEntity extends BlockEntity implements IntComponent, ServerTickingComponent  {

    private int wattLeft;
    private float kwh;

    public HeatBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        wattLeft = tag.getInt("wattage");
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        tag.putInt("wattage", wattLeft);
    }

    @Override
    public int getValue() {
        return wattLeft;
    }

    @Override
    public void setValue(int value) {
        wattLeft = value;
    }

    @Override
    public void clientTick() {

    }

    @Override
    public void serverTick() {
        wattLeft -= wattLeft -= kwh/1000;
    }
}
