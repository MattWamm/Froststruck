package net.mattwamm.froststruck.registries;

import net.mattwamm.froststruck.Froststruck;
import net.mattwamm.froststruck.entities.SledEntity;
import net.mattwamm.froststruck.items.SledItem;
import net.mattwamm.froststruck.items.Thermometer;
import net.mattwamm.froststruck.items.armor.InsulatedArmorMaterials;
import net.minecraft.entity.EquipmentSlot;
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
        for(ArmorMaterial armor : ArmorMaterials.values())
        {
            if(armor.getName().equals("chainmail") || armor.getName().equals("gold") || armor.getName().equals("turtle"))
                continue;

            for(EquipmentSlot slot : EquipmentSlot.values())
            {
                if(slot.getName().equals("mainhand") || slot.getName().equals("offhand"))
                    continue;
                registerItem(new ArmorItem(InsulatedArmorMaterials.valueOf( armor.getName().toUpperCase() + "_WOOL"), slot, new Item.Settings().group(ItemGroup.COMBAT)),armor.getName() + "_wool_" + slot.getName());
                registerItem(new ArmorItem(InsulatedArmorMaterials.valueOf( armor.getName().toUpperCase() + "_LAMA"), slot, new Item.Settings().group(ItemGroup.COMBAT)),armor.getName() + "_lama_wool_" + slot.getName());
                registerItem(new ArmorItem(InsulatedArmorMaterials.valueOf(armor.getName().toUpperCase() + "_DOWN"), slot, new Item.Settings().group(ItemGroup.COMBAT)),armor.getName() + "_down_" + slot.getName());
            }
        }
        OAK_SLED = registerItem(new SledItem(SledEntity.Type.OAK, new Item.Settings().maxCount(1).group(ItemGroup.TRANSPORTATION)), "oak_sled");
        SPRUCE_SLED = registerItem(new SledItem(SledEntity.Type.SPRUCE, new Item.Settings().maxCount(1).group(ItemGroup.TRANSPORTATION)), "spruce_sled");
        BIRCH_SLED = registerItem(new SledItem(SledEntity.Type.BIRCH, new Item.Settings().maxCount(1).group(ItemGroup.TRANSPORTATION)), "birch_sled");
        JUNGLE_SLED = registerItem(new SledItem(SledEntity.Type.JUNGLE, new Item.Settings().maxCount(1).group(ItemGroup.TRANSPORTATION)), "jungle_sled");
        ACACIA_SLED = registerItem(new SledItem(SledEntity.Type.ACACIA, new Item.Settings().maxCount(1).group(ItemGroup.TRANSPORTATION)), "acacia_sled");
        DARK_OAK_SLED = registerItem(new SledItem(SledEntity.Type.DARK_OAK, new Item.Settings().maxCount(1).group(ItemGroup.TRANSPORTATION)), "dark_oak_sled");
        THERMOMETER = registerItem(new Thermometer(new Item.Settings().maxCount(1).group(ItemGroup.MISC)), "thermometer");
    }

    private static Item registerItem(Item item, String name) {
        return Registry.register(Registry.ITEM, Froststruck.MODID + ":" + name, item);
    }
}
