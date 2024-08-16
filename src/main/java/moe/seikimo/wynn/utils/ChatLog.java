package moe.seikimo.wynn.utils;

import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.concurrent.atomic.AtomicBoolean;

public interface ChatLog {
    AtomicBoolean DO_LOG = new AtomicBoolean(false);

    /**
     * Sends a log message to the player.
     *
     * @param message The message to send.
     */
    static void log(String message) {
        if (!DO_LOG.get()) return;

        MessageUtils.sendMessage(Text.of("[LOG] " + message)
                .copy().formatted(Formatting.AQUA));
    }

    /**
     * Logs a formatted message.
     *
     * @param message The message to log.
     * @param args The arguments to format the message with.
     */
    static void log(String message, Object... args) {
        log(String.format(message, args));
    }
}
