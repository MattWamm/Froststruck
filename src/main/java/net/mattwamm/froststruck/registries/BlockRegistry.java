package net.mattwamm.froststruck.registries;


import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.mattwamm.froststruck.blocks.HeatSourceBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class BlockRegistry {

    public static BlockEntityType<HeatSourceBlockEntity> HEAT_SOURCE;
    private static final Block HEAT_BLOCK = new Block(FabricBlockSettings.of(Material.METAL).strength(4.0f).requiresTool());
    public static boolean register() {
        HEAT_SOURCE = Registry.register(Registry.BLOCK_ENTITY_TYPE, "froststruck:heat_source", FabricBlockEntityTypeBuilder.create(HeatSourceBlockEntity::new, HEAT_BLOCK).build(null));
        Registry.register(Registry.BLOCK, new Identifier("froststruck", "heat_source"), HEAT_BLOCK);
        Registry.register(Registry.ITEM, new Identifier("froststruck", "heat_source"), new BlockItem(HEAT_BLOCK, new FabricItemSettings().group(ItemGroup.MISC)));
        return true;
    }
}
