package net.mattwamm.froststruck.components;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

public final class EntityComponents implements EntityComponentInitializer {
    public static final ComponentKey<TemperatureComponent> TEMP =
            ComponentRegistry.getOrCreate(
                    new Identifier("froststruck:temperature"),
                    TemperatureComponent.class
            );
    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerFor(PlayerEntity.class, TEMP,(TemperatureComponent::new));
    }
}
