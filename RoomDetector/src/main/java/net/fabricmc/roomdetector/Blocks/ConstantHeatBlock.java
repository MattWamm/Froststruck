package net.fabricmc.roomdetector.Blocks;

import net.minecraft.block.Block;

public class ConstantHeatBlock extends AbstractHeatBlock {
    public ConstantHeatBlock(Settings settings) {
        super(settings);
        HeatOutPutKWH = 1000f;
        //TODO Auto-generated constructor stub
    }
}
