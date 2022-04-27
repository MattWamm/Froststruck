package net.mattwamm.froststruck.mixin;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.biome.OverworldBiomeCreator;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.DefaultBiomeFeatures;
import net.minecraft.world.gen.feature.MiscPlacedFeatures;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(OverworldBiomeCreator.class)
public class BiomeCreatorMixin {

//    @ModifyVariable(method = "createPlains", at = @At("HEAD"), ordinal = 1)
//    private static boolean plains(boolean snowy) {return true;}
//
//    @ModifyVariable(method = "createBeach", at = @At("HEAD"), ordinal = 0)
//    private static boolean beach(boolean snowy) {return true;}
//
//    @ModifyVariable(method = "createTaiga", at = @At("HEAD"), ordinal = 0)
//    private static boolean taiga(boolean cold) {return true;}
//
//    @ModifyVariable(method = "createRiver", at = @At("HEAD"), ordinal = 0)
//    private static boolean river(boolean frozen) {return true;}

    @ModifyArgs(method = "createBiome(Lnet/minecraft/world/biome/Biome$Precipitation;Lnet/minecraft/world/biome/Biome$Category;FFLnet/minecraft/world/biome/SpawnSettings$Builder;Lnet/minecraft/world/biome/GenerationSettings$Builder;Lnet/minecraft/sound/MusicSound;)Lnet/minecraft/world/biome/Biome;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/biome/OverworldBiomeCreator;createBiome(Lnet/minecraft/world/biome/Biome$Precipitation;Lnet/minecraft/world/biome/Biome$Category;FFIILnet/minecraft/world/biome/SpawnSettings$Builder;Lnet/minecraft/world/biome/GenerationSettings$Builder;Lnet/minecraft/sound/MusicSound;)Lnet/minecraft/world/biome/Biome;"))
    private static void Biome(Args args)
    {
        if(args.get(0) != Biome.Precipitation.SNOW) {
            SpawnSettings.Builder spawn = args.get(6);
            GenerationSettings.Builder generation = args.get(7);

            Biome.Category category = args.get(1);

            if (category == Biome.Category.OCEAN) {
                generation.feature(GenerationStep.Feature.SURFACE_STRUCTURES, MiscPlacedFeatures.ICEBERG_BLUE);
                generation.feature(GenerationStep.Feature.SURFACE_STRUCTURES, MiscPlacedFeatures.ICEBERG_BLUE);
            } else if (category != Biome.Category.UNDERGROUND) {
                //generation.feature(GenerationStep.Feature.SURFACE_STRUCTURES, MiscPlacedFeatures.ICE_PATCH);
                DefaultBiomeFeatures.addSnowyMobs(spawn);
                //DefaultBiomeFeatures.addSnowySpruceTrees(generation);
            }

            args.set(0, Biome.Precipitation.SNOW);
            args.set(2, 0.0f);
            args.set(3, 0.8f);

            spawn.creatureSpawnProbability(0.07f);

            args.set(6, spawn);
            args.set(7, generation);
        }
    }

//    @ModifyVariable(method = "createDesert")
}
