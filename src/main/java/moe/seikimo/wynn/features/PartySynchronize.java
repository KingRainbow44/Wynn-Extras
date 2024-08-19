package moe.seikimo.wynn.features;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.wynntils.utils.mc.McUtils;
import com.wynntils.utils.render.Texture;
import lombok.experimental.ExtensionMethod;
import lombok.extern.slf4j.Slf4j;
import moe.seikimo.jp2p.JP2P;
import moe.seikimo.wynn.ModConfig;
import moe.seikimo.wynn.utils.CastingExtensions;
import moe.seikimo.wynn.utils.ChatLog;
import moe.seikimo.wynn.utils.MessageUtils;
import moe.seikimo.wynn.utils.WaypointUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import static moe.seikimo.wynn.SyncProto.*;

@Slf4j
@ExtensionMethod(CastingExtensions.class)
public final class PartySynchronize {
    public static final long DEFAULT_ROOM_ID = 123456789L;

    private static JP2P connection;
    private static final Map<JP2P.Client, Identity> clients = new ConcurrentHashMap<>();

    /**
     * Connects to the P2P discovery server.
     */
    public static void initialize() {
        if (connection != null && connection.isConnectionAlive()) {
            connection.shutdown();
        }

        try {
            // Initialize a new connection.
            var config = ModConfig.get();
            if (config.getRoomId() == DEFAULT_ROOM_ID) {
                log.warn("The room ID is not set, we might we running into insecure territory!");
                MessageUtils.sendMessage(Text.translatable("wynn-extras.features.sync.insecure"));
                return;
            }

            var discovery = config.getDiscovery();
            connection = new JP2P(
                    "%s:%s".formatted(discovery.getAddress(), discovery.getPort()),
                    config.getRoomId());

            connection.setErrorHandler(PartySynchronize::onError);
            connection.onClientConnected(PartySynchronize::onClientConnected);
            connection.onClientDisconnected(PartySynchronize::onClientDisconnected);
            connection.onMessageReceived(PartySynchronize::onMessageReceived);

            // Start the connection.
            connection.start();
        } catch (IOException exception) {
            log.warn("Unable to connect to the P2P server.", exception);
        }
    }

    /**
     * Pings a location to the P2P server.
     *
     * @param x The X coordinate.
     * @param z The Z coordinate.
     */
    public static void pingLocation(double x, double z) {
        var notify = WaypointNotify.newBuilder()
                .setLocation(Location.newBuilder()
                        .setX(x).setY(0).setZ(z))
                .setExtended(false)
                .build();

        PartySynchronize.broadcastMessage(MessageId.WAYPOINT_NOTIFY, notify);
        ChatLog.log("Pinged a location to the P2P server.");
    }

    /**
     * Pings a location to the P2P server.
     *
     * @param x The X coordinate.
     * @param z The Z coordinate.
     * @param texture The texture to display.
     * @param text The text to display.
     */
    public static void pingLocation(
            double x, double z,
            Texture texture, String text) {
        var notify = WaypointNotify.newBuilder()
                .setLocation(Location.newBuilder()
                        .setX(x).setY(0).setZ(z))
                .setExtended(true)
                .setTexture(texture.ordinal())
                .setName(text)
                .build();

        PartySynchronize.broadcastMessage(MessageId.WAYPOINT_NOTIFY, notify);
        ChatLog.log("Pinged a POI to the P2P server.");
    }

    /**
     * Announces the client to the P2P server.
     */
    public static void announceSelf() {
        var profile = Objects.requireNonNull(MinecraftClient
                        .getInstance()
                        .player)
                .getGameProfile();
        var notify = SyncNotify.newBuilder()
                .setIdentity(Identity.newBuilder()
                        .setUsername(profile.getName())
                        .setUuid(profile.getId().toString()))
                .build();

        PartySynchronize.broadcastMessage(MessageId.SYNC_NOTIFY, notify);
        ChatLog.log("Announced the client to the P2P server.");
    }

    /**
     * Broadcasts a message to all connected clients.
     *
     * @param id The message ID.
     * @param message The message to broadcast.
     */
    private static void broadcastMessage(MessageId id, Message message) {
        if (!connection.isConnectionAlive()) {
            ChatLog.log("The connection is not alive, unable to broadcast the message.");
            log.warn("The connection is not alive, unable to broadcast the message.");
            return;
        }

        var wynnMessage = WynnMessage.newBuilder()
                .setId(id)
                .setData(message.toByteString())
                .build();

        try {
            connection.sendMessage(wynnMessage.toByteArray());
        } catch (IOException exception) {
            ChatLog.log("An exception occurred while sending a message: %s", exception.getMessage());
            log.warn("Unable to broadcast the message.", exception);
        }
    }

    /**
     * Invoked when a client connects to the P2P server.
     *
     * @param throwable The error that occurred.
     */
    private static void onError(Throwable throwable) {
        ChatLog.log("An error occurred in the P2P connection.");
        log.error("An error occurred in the P2P connection.", throwable);
    }

    /**
     * Invoked when a client connects to the P2P server.
     *
     * @param client The client's handle.
     */
    private static void onClientConnected(JP2P.Client client) {
        // Send a sync notification to the client.
        PartySynchronize.announceSelf();

        ChatLog.log("A client connected: %s:%s".formatted(client.address(), client.port()));
        log.info("Client connected: ({}:{})", client.address(), client.port());
    }

    /**
     * Invoked when a client disconnects from the P2P server.
     *
     * @param client The client's handle.
     */
    private static void onClientDisconnected(JP2P.Client client) {
        ChatLog.log("A client disconnected: %s:%s".formatted(client.address(), client.port()));
        log.info("Client disconnected: ({}:{})", client.address(), client.port());
    }

    /**
     * Invoked when a message is received from the P2P server.
     *
     * @param client The client's handle.
     * @param message The message that was received.
     */
    private static void onMessageReceived(JP2P.Client client, JP2P.Message message) {
        try {
            var decoded = WynnMessage.parseFrom(message.data());
            switch (decoded.getId()) {
                case SYNC_NOTIFY -> {
                    var notify = SyncNotify.parseFrom(decoded.getData());
                    clients.put(client, notify.getIdentity());

                    ChatLog.log("Sync notification received!");
                    log.info("Received a sync notification: {}", notify);
                }
                case WAYPOINT_NOTIFY -> {
                    var notify = WaypointNotify.parseFrom(decoded.getData());

                    // Fetch the identity of the client.
                    var identity = clients.get(client);
                    if (identity == null) {
                        ChatLog.log("Unknown client sent a waypoint notification.");
                        log.warn("Received a waypoint notification from an unknown client.");
                        return;
                    }

                    // Create the waypoint point.
                    var location = notify.getLocation();
                    if (!notify.getExtended()) {
                        WaypointUtils.pingLocation(
                                identity.getUsername() + "'s Waypoint",
                                location.asLoc());
                    } else {
                        WaypointUtils.pingLocation(
                                notify.getName(),
                                location.asLoc(),
                                Texture.values()[notify.getTexture()]);
                    }

                    // Play a sound to notify the player.
                    McUtils.playSoundUI(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP);

                    ChatLog.log("Received waypoint for (%.1f, %.1f) [extended = %s].",
                            location.getX(), location.getZ(), notify.getExtended());
                    log.info("Creating a waypoint for: {}", notify);
                }
                default -> {
                    ChatLog.log("Unknown message received: %s", decoded.getId());
                    log.warn("Unknown message ID: {}", decoded.getId());
                }
            }
        } catch (InvalidProtocolBufferException exception) {
            log.warn("Unable to decode the message.", exception);
        }
    }
}
