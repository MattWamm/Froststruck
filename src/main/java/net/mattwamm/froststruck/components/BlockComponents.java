package net.mattwamm.froststruck.components;

import dev.onyxstudios.cca.api.v3.block.BlockComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.block.BlockComponentInitializer;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import net.minecraft.block.entity.CampfireBlockEntity;
import net.minecraft.util.Identifier;

public class BlockComponents implements BlockComponentInitializer {
    public static final ComponentKey<RoomComponent> ROOM =
            ComponentRegistry.getOrCreate(
                    new Identifier("froststruck:blockroom"),
                    RoomComponent.class
            );

//    public static final ComponentKey<HeatSourceComponent> SOURCE =
//            ComponentRegistry.getOrCreate(
//                    new Identifier("froststruck:heatsource"),
//                    HeatSourceComponent.class
//            );

    @Override
    public void registerBlockComponentFactories(BlockComponentFactoryRegistry registry) {
              registry.registerFor(CampfireBlockEntity.class, ROOM, (RoomComponent::new) );
//            registry.registerFor(CampfireBlockEntity.class, SOURCE, (HeatSourceComponent::new) );
    }
}
