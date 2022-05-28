package net.mattwamm.froststruck.mixin.access;

import net.minecraft.world.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Biome.Builder.class)
public interface BuilderAccessor {


    //access variables in the biome builder class to force every biome to be snow including the badlands and desert which where resisting.
    @Accessor("precipitation")
    public void setPrecipitation(Biome.Precipitation precipitation);
    @Accessor("temperature")
    public void setTemp(Float temperature);
}

