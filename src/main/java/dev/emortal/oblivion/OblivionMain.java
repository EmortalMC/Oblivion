package dev.emortal.oblivion;

import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.GameMode;
import net.minestom.server.event.player.PlayerLoginEvent;
import net.minestom.server.event.player.PlayerSpawnEvent;
import net.minestom.server.extras.MojangAuth;
import net.minestom.server.extras.bungee.BungeeCordProxy;
import net.minestom.server.extras.velocity.VelocityProxy;
import net.minestom.server.network.packet.client.play.ClientSetRecipeBookStatePacket;
import net.minestom.server.network.packet.server.CachedPacket;
import net.minestom.server.network.packet.server.play.CameraPacket;
import net.minestom.server.network.packet.server.play.SpawnEntityPacket;
import net.minestom.server.utils.NamespaceID;
import net.minestom.server.world.DimensionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.UUID;

public class OblivionMain {

    private static final Path configPath = Path.of("./server.properties");
    private static final Logger logger = LoggerFactory.getLogger("Oblivion");

    public static void main(String... args) {

        System.setProperty("minestom.tps", "1");
        System.getProperty("minestom.chunk-view-distance", "2");
        System.getProperty("minestom.entity-view-distance", "0");

        final var properties = loadProperties();
        final var server = MinecraftServer.init();

        final var dimension = DimensionType.builder(NamespaceID.from("minecraft:oblivion"))
                .skylightEnabled(false)
                .ceilingEnabled(false)
                .fixedTime(null)
                .effects(properties.getProperty("end-dimension").equals("true") ? "the_end" : "")
                .ambientLight(1.0f)
                .height(16)
                .minY(0)
                .logicalHeight(16)
                .build();

        MinecraftServer.getDimensionTypeManager().addDimension(dimension);

        final var instance = new LightInstance(UUID.randomUUID(), dimension);
        MinecraftServer.getInstanceManager().registerInstance(instance);
        instance.setTimeRate(0);
        instance.setTimeUpdate(null);
        instance.enableAutoChunkLoad(false);

        // Client requires a few chunks to load the world
        // also apparently requires a 3x3 area to allow for refreshAbilities()?
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                instance.loadChunk(x, z);
            }
        }

        final var entityId = Entity.generateId();
        final var entityPacket = new SpawnEntityPacket(entityId, UUID.randomUUID(), EntityType.ARMOR_STAND.id(), Pos.ZERO, 0f, 0, (short) 0, (short) 0, (short) 0);
        final var cameraPacket = new CameraPacket(entityId);

        final var globalEvent = MinecraftServer.getGlobalEventHandler();

        // Use custom LightPlayer
        MinecraftServer.getConnectionManager().setPlayerProvider(LightPlayer::new);
        // Ignore warning when player opens recipe book
        MinecraftServer.getPacketListenerManager().setListener(ClientSetRecipeBookStatePacket.class, (a, b) -> {});

        globalEvent.addListener(PlayerLoginEvent.class, e -> {
            e.setSpawningInstance(instance);
        });
        globalEvent.addListener(PlayerSpawnEvent.class, e -> {
            e.getPlayer().setGameMode(GameMode.SPECTATOR);
            e.getPlayer().sendPacket(entityPacket);
            e.getPlayer().sendPacket(cameraPacket);
        });

        final var onlineMode = Boolean.parseBoolean(properties.getProperty("online-mode"));
        final var address = properties.getProperty("address");
        final var port = Integer.parseInt(properties.getProperty("port"));
        final var compressionThreshold = Integer.parseInt(properties.getProperty("compression-threshold"));

        final var proxy = properties.getProperty("proxy").toLowerCase();
        final var proxySecret = properties.getProperty("proxy-secret");

        switch (proxy) {
            case "velocity" -> {
                VelocityProxy.enable(proxySecret);
                logger.info("Enabling velocity forwarding");
            }
            case "bungee" -> {
                BungeeCordProxy.enable();
                logger.info("Enabling bungee forwarding");
            }
        }


        if (onlineMode) {
            logger.info("Starting server with online mode enabled!");
            MojangAuth.init();
        }

        MinecraftServer.setCompressionThreshold(compressionThreshold);

        server.start(address, port);
    }

    public static Properties loadProperties() {
        var properties = new Properties();
        try {
            if (Files.exists(configPath)) {
                properties.load(Files.newInputStream(configPath));
            } else {
                var inputStream = OblivionMain.class.getClassLoader().getResourceAsStream("server.properties");
                properties.load(inputStream);
                properties.store(Files.newOutputStream(configPath), "Minestom " + MinecraftServer.VERSION_NAME);
            }
        } catch (IOException e) {
            MinecraftServer.getExceptionManager().handleException(e);
        }

        return properties;
    }

}
