package net.mattwamm.froststruck.mixin;

import net.minecraft.world.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Biome.Builder.class)
public class BiomeBuilderMixin {

    @Inject(method = "precipitation", at = @At("TAIL"))
    private void injected(Biome.Precipitation precipitation, CallbackInfoReturnable<Biome.Builder> cir){
        if(precipitation != Biome.Precipitation.SNOW)
            ((BuilderAccessor)(Biome.Builder)(Object)this).setPrecipitation(Biome.Precipitation.SNOW);
    }
    @Inject(method = "temperature", at = @At("TAIL"))
    private void injected2(float temperature, CallbackInfoReturnable<Biome.Builder> cir){
        if(temperature > 0.0f)
            ((BuilderAccessor)(Biome.Builder)(Object)this).setTemp(0.0f);
    }



}
