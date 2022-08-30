package net.mattwamm.froststruck.blocks.blockentity;

import net.mattwamm.froststruck.registries.BlockRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class ModularComponentBlockEntity extends BlockEntity {

    //Down, Up, North, South, West, East
    private BlockState[] attachments = new BlockState[6];

    public ModularComponentBlockEntity(BlockPos pos, BlockState state) {
        super(BlockRegistry.HEAT_SOURCE, pos, state);
        World world = this.getWorld();
        assert world != null;
        for (Direction dir : Direction.values()) {
            this.attachments[dir.ordinal()] = world.getBlockState(this.pos.offset(dir));
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        
        super.writeNbt(nbt);
    }
}
