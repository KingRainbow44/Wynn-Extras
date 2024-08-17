package moe.seikimo.wynn.features;

import moe.seikimo.wynn.ModConfig;
import moe.seikimo.wynn.WynnClient;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.util.ClickType;

public final class AutoClicker {
    private static KeyBinding attackKey;
    private static long ticks = 0;

    /**
     * Prepares the auto clicker.
     */
    public static void initialize() {
        AutoClicker.attackKey = MinecraftClient.getInstance()
                .options.attackKey;

        ClientTickEvents.END_CLIENT_TICK.register(AutoClicker::onTick);
    }

    /**
     * Invoked every Minecraft client tick.
     *
     * @param client The Minecraft client.
     */
    private static void onTick(MinecraftClient client) {
        if (client.player == null) return;

        var config = ModConfig.get().getAutoClicker();
        if (!config.isEnabled()) return;

        if (!WynnClient.getClickQueue().isEmpty()) return;

        // Check if the auto clicker should run.
        if (ticks++ % config.getDelay() != 0) return;
        ticks = 0;

        if (AutoClicker.attackKey.isPressed()) {
            WynnClient.getClickQueue().click(ClickType.LEFT);
        }
    }
}
