package net.mattwamm.froststruck.blocks;

import net.minecraft.block.Block;

public class InsulationBlock extends Block {

    private final int insulationLvl;

    public InsulationBlock(Settings settings, int insulationLvl) {
        super(settings);
        this.insulationLvl = insulationLvl;
    }




}
