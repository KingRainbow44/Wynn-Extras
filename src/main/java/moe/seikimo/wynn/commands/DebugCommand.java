package moe.seikimo.wynn.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import moe.seikimo.wynn.features.PartySynchronize;
import moe.seikimo.wynn.utils.ChatLog;
import moe.seikimo.wynn.utils.Parser;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public interface DebugCommand {
    /**
     * Registers a command with the dispatcher.
     *
     * @param dispatcher The command dispatcher.
     */
    static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(
                literal("debug")
                        .then(literal("coordinates")
                                .then(argument("coordinates", greedyString())
                                        .executes(DebugCommand::coordinates)))
                        .then(literal("log").executes(DebugCommand::log))
                        .then(literal("auth").executes(DebugCommand::authenticate))
                        .then(literal("reauth").executes(DebugCommand::authenticate))
                        .then(literal("authenticate").executes(DebugCommand::authenticate))
                        .executes(DebugCommand::invalid)
        );
    }

    static int invalid(CommandContext<FabricClientCommandSource> context) {
        context.getSource().sendError(Text.translatable("wynn-extras.commands.debug.invalid"));
        return 1;
    }

    static int coordinates(CommandContext<FabricClientCommandSource> context) {
        var coordinates = context.getArgument("coordinates", String.class);
        var parsed = Parser.parseCoordinates(coordinates);

        if (parsed == null) {
            context.getSource().sendError(Text.translatable("wynn-extras.commands.debug.coordinates.invalid"));
        } else {
            context.getSource().sendFeedback(Text.translatable(
                    "wynn-extras.commands.debug.coordinates",
                    parsed.x, parsed.y, parsed.z));
        }

        return 1;
    }

    static int log(CommandContext<FabricClientCommandSource> context) {
        var currentState = ChatLog.DO_LOG.get();
        context.getSource().sendFeedback(Text.translatable("wynn-extras.commands.debug.log")
                .formatted(currentState ? Formatting.RED : Formatting.GREEN));
        ChatLog.DO_LOG.set(!currentState);

        return 1;
    }

    static int authenticate(CommandContext<FabricClientCommandSource> context) {
        PartySynchronize.announceSelf();
        context.getSource().sendFeedback(Text.translatable("wynn-extras.commands.debug.auth")
                .formatted(Formatting.YELLOW));

        return 1;
    }
}
