import net.minestom.server.attribute.Attribute;
import net.minestom.server.entity.Player;
import net.minestom.server.network.player.PlayerConnection;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class LightPlayer extends Player {
    public LightPlayer(@NotNull UUID uuid, @NotNull String username, @NotNull PlayerConnection playerConnection) {
        super(uuid, username, playerConnection);



        setAutoViewable(false);
    }

    @Override
    public void update(long time) {
        // For keep alive
        interpretPacketQueue();
    }

    @Override
    public void handleVoid() {

    }
}
