package net.mattwamm.froststruck.registries;


import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.mattwamm.froststruck.blocks.HeatSourceBlockEntity;
import net.mattwamm.froststruck.blocks.blockentity.ModularComponentBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class BlockRegistry {

    public static BlockEntityType<HeatSourceBlockEntity> HEAT_SOURCE;
//    public static BlockEntityType<ModularComponentBlockEntity> MOD_BLOCK_ENTITY;
    private static final Block HEAT_BLOCK = new Block(FabricBlockSettings.of(Material.METAL).strength(4.0f).requiresTool());
//    private static final Block MODULAR_COMPONENT_BLOCK = new Block(FabricBlockSettings.of(Material.METAL).strength(4.0f).requiresTool());
    public static boolean register() {
//        MOD_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, "froststruck:modular_component", FabricBlockEntityTypeBuilder.create(ModularComponentBlockEntity::new, MODULAR_COMPONENT_BLOCK).build(null));
        HEAT_SOURCE = Registry.register(Registry.BLOCK_ENTITY_TYPE, "froststruck:heat_source", FabricBlockEntityTypeBuilder.create(HeatSourceBlockEntity::new, HEAT_BLOCK).build(null));


//      Don't forget to register the block AND the item for it.
        Registry.register(Registry.BLOCK, new Identifier("froststruck", "heat_source"), HEAT_BLOCK);
        Registry.register(Registry.ITEM, new Identifier("froststruck", "heat_source"), new BlockItem(HEAT_BLOCK, new FabricItemSettings().group(ItemGroup.MISC)));
        return true;
    }
}
