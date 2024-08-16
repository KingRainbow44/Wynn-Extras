package moe.seikimo.wynn.features;

import lombok.experimental.ExtensionMethod;
import moe.seikimo.wynn.utils.CastingExtensions;
import moe.seikimo.wynn.utils.MessageUtils;
import moe.seikimo.wynn.utils.Parser;
import moe.seikimo.wynn.utils.WaypointUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWWindowFocusCallback;

/**
 * Feature: When the window is refocused, check the clipboard.
 *          If the clipboard contains coordinates, make a new waypoint.
 */
@ExtensionMethod(CastingExtensions.class)
public final class ClipboardCoordinates {
    /**
     * The current window handle.
     */
    private static long pWindow;

    private static GLFWWindowFocusCallback pCallback;

    /**
     * Registers callbacks for the window.
     *
     * @param pWindow A pointer to the window handle.
     */
    public static void initialize(long pWindow) {
        ClipboardCoordinates.pCallback = GLFW.glfwSetWindowFocusCallback(
                ClipboardCoordinates.pWindow = pWindow,
                ClipboardCoordinates::onWindowFocus
        );
    }

    /**
     * Invoked when the window's focus state changes.
     *
     * @param pWindow A pointer to the window handle.
     * @param focused Whether the window is focused.
     */
    private static void onWindowFocus(long pWindow, boolean focused) {
        if (ClipboardCoordinates.pWindow == pWindow && focused) {
            ClipboardCoordinates.readClipboard();
        }

        if (ClipboardCoordinates.pCallback != null) {
            pCallback.invoke(pWindow, focused);
        }
    }

    /**
     * Invoked when the window is focused.
     */
    private static void readClipboard() {
        // Get the coordinates from the clipboard.
        var keyboard = MinecraftClient.getInstance().keyboard;
        var content = keyboard.getClipboard();
        var coordinates = Parser.parseCoordinates(content);

        // Skip if the coordinates aren't valid.
        if (coordinates == null) return;

        // Check if the waypoint already exists.
        var location = coordinates.asLoc();
        if (WaypointUtils.waypointExists(location)) {
            return;
        }

        // Create a new waypoint.
        WaypointUtils.createWaypoint(content, location);
        // Log the message to the player.
        MessageUtils.sendMessage(Text.translatable(
                "wynn-extras.features.clipboard.added",
                coordinates.x, coordinates.y, coordinates.z
        ).formatted(Formatting.YELLOW));

        // Clear the clipboard.
        keyboard.setClipboard(" ");
    }
}
