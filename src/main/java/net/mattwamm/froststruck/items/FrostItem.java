package net.mattwamm.froststruck.items;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class FrostItem extends Item {

    public FrostItem(ItemGroup group, String identifierPath)
    {
        super(new FabricItemSettings().group(group));
        this.registerItem(identifierPath);
    }
    public FrostItem(ItemGroup group, String identifierPath, Integer burnTime)
    {
        super(new FabricItemSettings().group(group));
        this.registerItem(identifierPath, burnTime);
    }
    public void registerItem(String IdentifierPath)
    {
        Registry.register(Registry.ITEM, new Identifier("froststruck:" + IdentifierPath), this);
    }
    public void registerItem(String IdentifierPath, Integer BurnTime)
    {
        Registry.register(Registry.ITEM, new Identifier("froststruck:" + IdentifierPath), this);
        FuelRegistry.INSTANCE.add(this,BurnTime);
    }


}
