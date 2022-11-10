package dev.emortal.oblivion;

import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.world.DimensionType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class LightInstance extends InstanceContainer {

    public LightInstance(@NotNull UUID uniqueId, @NotNull DimensionType dimensionType) {
        super(uniqueId, dimensionType);
    }

    @Override
    public void tick(long time) {

    }
}
