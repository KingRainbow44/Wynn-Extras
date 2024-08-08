package moe.seikimo.wynn;

import moe.seikimo.wynn.commands.DebugCommand;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;

public final class WynnClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            DebugCommand.register(dispatcher);
        });
    }
}
