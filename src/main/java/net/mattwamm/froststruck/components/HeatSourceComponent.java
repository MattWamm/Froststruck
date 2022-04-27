package net.mattwamm.froststruck.components;

import net.mattwamm.froststruck.util.IsolatedRoom;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;

public class HeatSourceComponent implements BlockComponent{

    public int Room;
    public int InsulationPercentage;
    private BlockEntity provider;
    public HeatSourceComponent(BlockEntity provider) {this.provider = provider;}
    @Override
    public int getValue() {
        return 0;
    }
    @Override
    public void setValue(IsolatedRoom room) {
    }
    @Override
    public void readFromNbt(NbtCompound tag) {
    }
    @Override
    public void writeToNbt(NbtCompound tag) {
    }
}
