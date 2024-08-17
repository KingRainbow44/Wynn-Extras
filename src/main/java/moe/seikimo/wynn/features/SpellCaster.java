package moe.seikimo.wynn.features;

import moe.seikimo.wynn.Keybinds;
import moe.seikimo.wynn.ModConfig;
import moe.seikimo.wynn.WynnClient;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.util.ClickType;

import java.util.List;

import static net.minecraft.util.ClickType.*;

public final class SpellCaster {
    private static final List<ClickType> SPELL_1 = List.of(RIGHT, LEFT, RIGHT);
    private static final List<ClickType> SPELL_2 = List.of(RIGHT, RIGHT, RIGHT);
    private static final List<ClickType> SPELL_3 = List.of(RIGHT, LEFT, LEFT);
    private static final List<ClickType> SPELL_4 = List.of(RIGHT, RIGHT, LEFT);
    private static final List<ClickType> MELEE = List.of(LEFT);

    /**
     * Registers tick listeners for keybinds.
     */
    public static void initialize() {
        ClientTickEvents.END_CLIENT_TICK.register(SpellCaster::onTick);
    }

    /**
     * Invoked when the client ticks.
     *
     * @param client The client instance.
     */
    private static void onTick(MinecraftClient client) {
        SpellCaster.checkSpell(Keybinds.SPELL_1, SpellCaster.SPELL_1);
        SpellCaster.checkSpell(Keybinds.SPELL_2, SpellCaster.SPELL_2);
        SpellCaster.checkSpell(Keybinds.SPELL_3, SpellCaster.SPELL_3);
        SpellCaster.checkSpell(Keybinds.SPELL_4, SpellCaster.SPELL_4);

        if (!ModConfig.get().getAutoClicker().isEnabled()) {
            SpellCaster.checkSpell(Keybinds.MELEE, SpellCaster.MELEE);
        }
    }

    /**
     * Checks if the player is casting a spell.
     *
     * @param bind The keybind to check.
     * @param sequence The sequence of clicks to check.
     */
    private static void checkSpell(KeyBinding bind, List<ClickType> sequence) {
        if (!bind.isPressed()) return;
        bind.setPressed(false);

        // Input the sequence of clicks.
        sequence.forEach(WynnClient.getClickQueue()::click);
    }
}
