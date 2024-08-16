package moe.seikimo.wynn;

import lombok.Data;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.CollapsibleObject;
import moe.seikimo.wynn.utils.ChatLog;

import static moe.seikimo.wynn.features.PartySynchronize.DEFAULT_ROOM_ID;

@Data
@Config(name = "wynn-extras")
public final class ModConfig implements ConfigData {
    @ConfigEntry.Gui.Tooltip
    private long roomId = DEFAULT_ROOM_ID;

    @ConfigEntry.Gui.Tooltip
    private boolean enableLog = false;

    @CollapsibleObject
    @ConfigEntry.Gui.Tooltip
    private Discovery discovery = new Discovery();

    @Data
    public static final class Discovery {
        private String address = "127.0.0.1";
        private int port = 4435;
    }

    @Override
    public void validatePostLoad() {
        if (this.isEnableLog()) {
            ChatLog.DO_LOG.set(true);
        }
    }

    /**
     * @return The current configuration instance.
     */
    public static ModConfig get() {
        return AutoConfig.getConfigHolder(ModConfig.class).getConfig();
    }

    /**
     * Saves the configuration to disk.
     */
    public static void save() {
        AutoConfig.getConfigHolder(ModConfig.class).save();
    }
}
