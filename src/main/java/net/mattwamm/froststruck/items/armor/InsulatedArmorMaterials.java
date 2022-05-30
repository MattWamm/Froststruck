package net.mattwamm.froststruck.items.armor;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ArmorMaterials;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.ItemTags;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Lazy;

import java.util.function.Supplier;

public enum InsulatedArmorMaterials implements ArmorMaterial {
    LEATHER_WOOL(ArmorMaterials.LEATHER, 2, "wool"),
    LEATHER_LAMA(ArmorMaterials.LEATHER, 3, "lama_wool"),
    LEATHER_DOWN(ArmorMaterials.LEATHER, 4, "down"),
    IRON_WOOL(ArmorMaterials.IRON, 1, "wool"),
    IRON_LAMA(ArmorMaterials.IRON, 2, "lama_wool"),
    IRON_DOWN(ArmorMaterials.IRON, 3, "down"),
    DIAMOND_WOOL(ArmorMaterials.DIAMOND, 1, "wool"),
    DIAMOND_LAMA(ArmorMaterials.DIAMOND, 2, "lama_wool"),
    DIAMOND_DOWN(ArmorMaterials.DIAMOND, 3, "down"),
    NETHERITE_WOOL(ArmorMaterials.NETHERITE, 0, "wool"),
    NETHERITE_LAMA(ArmorMaterials.NETHERITE, 1, "lama_wool"),
    NETHERITE_DOWN(ArmorMaterials.NETHERITE, 2, "down"),
    WOOL("wool",3,new int[]{1,2,3,1},1 , SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 0.0f, 0, Ingredient.fromTag(ItemTags.WOOL),5 ),
    LAMA_WOOL("lama_wool",3,new int[]{1,2,3,1},1 , SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 0.0f, 0, Ingredient.fromTag(ItemTags.WOOL),6),
    LAMA_DOWN("lama_down",3,new int[]{1,2,3,1},1 , SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 0.0f, 0, Ingredient.fromTag(ItemTags.WOOL),7),
    ;
    private static final int[] BASE_DURABILITY = new int[]{13, 15, 16, 11};
    private final int[] protectionAmounts;
    private final String name;
    private final int durabilityMultiplier;
    private final int enchantability;
    private final SoundEvent equipSound;
    private final float toughness;
    private final float knockbackResistance;
    private final Ingredient repairIngredient;
    private final float insulationValue;

    private InsulatedArmorMaterials(String name, int durabilityMultiplier, int[] protectionAmounts, int enchantability, SoundEvent equipSound, float toughness, float knockbackResistance, Ingredient repairIngredient, float insulationValue) {
        this.name = name;
        this.durabilityMultiplier = durabilityMultiplier;
        this.protectionAmounts = protectionAmounts; // boots, leggings, chestplate, helmet
        this.enchantability = enchantability;
        this.equipSound = equipSound;
        this.toughness = toughness;
        this.knockbackResistance = knockbackResistance;
        this.repairIngredient = repairIngredient;
        this.insulationValue = insulationValue;
    }

    private InsulatedArmorMaterials(ArmorMaterial baseMaterial, float insulationValue, String insulationType) {
        this.name = insulationType + "_" + baseMaterial.getName();
        this.durabilityMultiplier = baseMaterial.getDurability(EquipmentSlot.FEET) / 13; // Every armor uses the same base durability. this way I can avoid using a mixin or access widener.
        this.protectionAmounts =
        new int[]{
                baseMaterial.getProtectionAmount(EquipmentSlot.FEET),
                baseMaterial.getProtectionAmount(EquipmentSlot.LEGS),
                baseMaterial.getProtectionAmount(EquipmentSlot.CHEST),
                baseMaterial.getProtectionAmount(EquipmentSlot.HEAD)
        }; // boots, leggings, chestplate, helmet
        this.enchantability = baseMaterial.getEnchantability();
        this.equipSound = getEquipSound();
        this.toughness = getToughness();
        this.knockbackResistance = getKnockbackResistance();
        this.repairIngredient = baseMaterial.getRepairIngredient();
        this.insulationValue = insulationValue;
    }


    @Override
    public int getDurability(EquipmentSlot slot) {
        return BASE_DURABILITY[slot.getEntitySlotId()] * this.durabilityMultiplier;
    }

    @Override
    public int getProtectionAmount(EquipmentSlot slot) {
        return this.protectionAmounts[slot.getEntitySlotId()];
    }

    @Override
    public int getEnchantability() {
        return this.enchantability;
    }

    @Override
    public SoundEvent getEquipSound() {
        return this.equipSound;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return this.repairIngredient;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public float getToughness() {
        return this.toughness;
    }

    @Override
    public float getKnockbackResistance() {
        return this.knockbackResistance;
    }


}
