package net.mattwamm.froststruck.components;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.ClientTickingComponent;
import dev.onyxstudios.cca.api.v3.component.tick.ServerTickingComponent;
import net.mattwamm.froststruck.Froststruck;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.MessageType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import static java.lang.Math.floor;
import static java.lang.Math.min;

public class TemperatureComponent implements IntComponent, AutoSyncedComponent{

    private final ComponentKey<TemperatureComponent> KEY = Components.TEMP;
    private PlayerEntity provider;
    private int ambientTemperature;

    public TemperatureComponent(PlayerEntity provider) {this.provider = provider;}

    @Override public int getValue()
    {
        return this.ambientTemperature ;
    }

    @Override public void setValue(int value)
    {
        this.ambientTemperature = value;
        KEY.sync(this.provider);
    }

    @Override
    public void clientTick() {
        Double distance = floor(Froststruck.spawn.getSquaredDistance(provider.getPos()));
        this.ambientTemperature = (int)min(0, -(floor(distance / 5000)));
    }

    @Override public void readFromNbt(NbtCompound tag)
    {
        this.ambientTemperature = tag.getInt("value");
    }
    @Override public void writeToNbt(NbtCompound tag)
    {
        tag.putInt("value", this.ambientTemperature);
    }
}
