package net.mattwamm.froststruck.components;

import dev.onyxstudios.cca.api.v3.component.Component;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.ClientTickingComponent;
import net.minecraft.nbt.NbtCompound;

public interface IntComponent extends ClientTickingComponent {
    int getValue();
    void setValue(int value);
}
