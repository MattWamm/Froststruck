package net.mattwamm.froststruck.components;

import net.mattwamm.froststruck.util.IsolatedRoom;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;

public class RoomComponent implements BlockComponent{

    public IsolatedRoom room = null;
    public BlockEntity provider;
    public RoomComponent(BlockEntity provider) {this.provider = provider;}
    @Override
    public int getValue() {
        return 0;
    }

    public IsolatedRoom getRoom()
    {
        return this.room;
    }
    public void setValue(IsolatedRoom room) {
        this.room = room;
    }
    @Override
    public void readFromNbt(NbtCompound tag) {
    }
    @Override
    public void writeToNbt(NbtCompound tag) {
    }
}
