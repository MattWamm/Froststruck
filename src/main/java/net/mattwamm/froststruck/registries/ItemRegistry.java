package net.mattwamm.froststruck.registries;

import net.mattwamm.froststruck.items.Thermometer;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SwordItem;
import net.minecraft.screen.ScreenHandler;

public class ItemRegistry {

    public static void register()
    {
        new Thermometer(ItemGroup.TOOLS,"thermometer");
    }
}
