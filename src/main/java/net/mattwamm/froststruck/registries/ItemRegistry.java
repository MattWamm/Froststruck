package net.mattwamm.froststruck.registries;

import net.mattwamm.froststruck.Froststruck;
import net.mattwamm.froststruck.entities.SledEntity;
import net.mattwamm.froststruck.items.SledItem;
import net.mattwamm.froststruck.items.Thermometer;
import net.minecraft.item.*;
import net.minecraft.util.registry.Registry;

public class ItemRegistry {

    public static Item OAK_SLED;
    public static Item SPRUCE_SLED;
    public static Item BIRCH_SLED;
    public static Item JUNGLE_SLED;
    public static Item ACACIA_SLED;
    public static Item DARK_OAK_SLED;
    public static Item THERMOMETER;


    public static void register()
    {
        OAK_SLED = registerItem(new SledItem(SledEntity.Type.OAK, new Item.Settings().maxCount(1).group(ItemGroup.TRANSPORTATION)), "oak_sled");
        SPRUCE_SLED = registerItem(new SledItem(SledEntity.Type.OAK, new Item.Settings().maxCount(1).group(ItemGroup.TRANSPORTATION)), "spruce_sled");
        BIRCH_SLED = registerItem(new SledItem(SledEntity.Type.OAK, new Item.Settings().maxCount(1).group(ItemGroup.TRANSPORTATION)), "birch_sled");
        JUNGLE_SLED = registerItem(new SledItem(SledEntity.Type.OAK, new Item.Settings().maxCount(1).group(ItemGroup.TRANSPORTATION)), "jungle_sled");
        ACACIA_SLED = registerItem(new SledItem(SledEntity.Type.OAK, new Item.Settings().maxCount(1).group(ItemGroup.TRANSPORTATION)), "acacia_sled");
        DARK_OAK_SLED = registerItem(new SledItem(SledEntity.Type.OAK, new Item.Settings().maxCount(1).group(ItemGroup.TRANSPORTATION)), "dark_oak_sled");
        THERMOMETER = registerItem(new Thermometer(new Item.Settings().maxCount(1).group(ItemGroup.MISC)), "thermometer");
    }

    private static Item registerItem(Item item, String name) {
        Registry.register(Registry.ITEM, Froststruck.MODID + ":" + name, item);
        return item;
    }
}
