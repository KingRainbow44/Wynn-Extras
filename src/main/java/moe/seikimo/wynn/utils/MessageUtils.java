package moe.seikimo.wynn.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public interface MessageUtils {
    /**
     * Sends a message to the player.
     *
     * @param message The message as a string.
     */
    static void sendMessage(Text message) {
        MessageUtils.sendMessage(message, false);
    }

    /**
     * Sends a message to the player.
     *
     * @param message The message as a component.
     * @param overlay Should the message be shown above the hotbar?
     */
    static void sendMessage(Text message, boolean overlay) {
        var client = MinecraftClient.getInstance();
        client.execute(() -> {
            if (client.player != null) {
                client.player.sendMessage(message, overlay);
            }
        });
    }
}
