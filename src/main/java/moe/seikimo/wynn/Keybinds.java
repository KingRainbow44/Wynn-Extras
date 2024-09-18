package moe.seikimo.wynn;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public final class Keybinds {
    public static final KeyBinding SPELL_1 = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.wynn-extras.spell.first",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_UNKNOWN,
            "key.category.wynn-extras"
    ));

    public static final KeyBinding SPELL_2 = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.wynn-extras.spell.second",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_UNKNOWN,
            "key.category.wynn-extras"
    ));

    public static final KeyBinding SPELL_3 = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.wynn-extras.spell.third",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_UNKNOWN,
            "key.category.wynn-extras"
    ));

    public static final KeyBinding SPELL_4 = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.wynn-extras.spell.fourth",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_UNKNOWN,
            "key.category.wynn-extras"
    ));

    public static final KeyBinding MELEE = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.wynn-extras.spell.melee",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_UNKNOWN,
            "key.category.wynn-extras"
    ));

    /**
     * A no-operation method to initialize the keybinds.
     */
    public static void initialize() {
        // This is a static initializer.
    }
}
