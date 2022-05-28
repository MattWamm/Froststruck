package net.mattwamm.froststruck.Blocks;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.Item;

public class AbstractHeatBlock extends AbstractBlock {
    public AbstractHeatBlock(Settings settings) {
        super(settings);
    }

    @Override
    public Item asItem() {
        return null;
    }

    @Override
    protected Block asBlock() {
        return null;
    }
}
