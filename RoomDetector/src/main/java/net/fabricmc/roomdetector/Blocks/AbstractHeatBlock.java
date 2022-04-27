package net.fabricmc.roomdetector.Blocks;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;

public abstract class AbstractHeatBlock extends Block{

    protected Float HeatOutPutKWH;
    public Float GetHeatOutput()
    {
        return HeatOutPutKWH;
    }
    public AbstractHeatBlock(Settings settings) {
        super(settings);
        //TODO Auto-generated constructor stub
    }
    
}
