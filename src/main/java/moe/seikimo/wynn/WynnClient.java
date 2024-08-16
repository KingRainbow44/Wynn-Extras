package moe.seikimo.wynn;

import com.wynntils.core.WynntilsMod;
import com.wynntils.models.worlds.event.WorldStateEvent;
import com.wynntils.models.worlds.type.WorldState;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import moe.seikimo.wynn.commands.DebugCommand;
import moe.seikimo.wynn.features.PartySynchronize;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.neoforged.bus.api.SubscribeEvent;

public final class WynnClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            DebugCommand.register(dispatcher);
        });

        ClientLifecycleEvents.CLIENT_STARTED.register(client ->
                WynntilsMod.registerEventListener(this));
    }

    @SubscribeEvent
    public void onServerConnect(WorldStateEvent event) {
        if (event.getNewState() != WorldState.WORLD) return;

        PartySynchronize.initialize();
    }
}
