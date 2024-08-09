package moe.seikimo.wynn.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import moe.seikimo.wynn.utils.Parser;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;

public interface DebugCommand {
    /**
     * Registers a command with the dispatcher.
     *
     * @param dispatcher The command dispatcher.
     */
    static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(
                ClientCommandManager.literal("debug")
                        .then(ClientCommandManager.literal("coordinates")
                                .then(ClientCommandManager.argument("coordinates", greedyString())
                                        .executes(DebugCommand::coordinates)))
                        .executes(DebugCommand::invalid)
        );
    }

    static int invalid(CommandContext<FabricClientCommandSource> context) {
        context.getSource().sendError(Text.translatable("magix-wynn.commands.debug.invalid"));
        return 1;
    }

    static int coordinates(CommandContext<FabricClientCommandSource> context) {
        var coordinates = context.getArgument("coordinates", String.class);
        var parsed = Parser.parseCoordinates(coordinates);

        if (parsed == null) {
            context.getSource().sendError(Text.translatable("magix-wynn.commands.debug.coordinates.invalid"));
        } else {
            context.getSource().sendFeedback(Text.translatable(
                    "magix-wynn.commands.debug.coordinates",
                    parsed.x, parsed.y, parsed.z));
        }

        return 1;
    }
}
