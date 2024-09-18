package moe.seikimo.wynn.mixin;

import com.wynntils.core.WynntilsMod;
import com.wynntils.core.components.Model;
import com.wynntils.core.components.Models;
import com.wynntils.core.components.Services;
import com.wynntils.core.text.StyledText;
import com.wynntils.models.players.PartyModel;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.regex.Pattern;

@Mixin(PartyModel.class)
public abstract class PartyModelMixin extends Model {
    @Shadow(remap = false) @Final private static Pattern PARTY_CREATE_SELF;

    @Shadow(remap = false) @Final private static Pattern PARTY_JOIN_SELF;

    @Shadow(remap = false) @Final private static Pattern PARTY_JOIN_OTHER;

    protected PartyModelMixin(List<Model> dependencies) {
        super(dependencies);
    }

    @Inject(method = "tryParsePartyMessages", at = @At("HEAD"), remap = false)
    public void onMessageReceived(StyledText styledText, CallbackInfoReturnable<Boolean> cir) {
        var shouldAuth =
                styledText.matches(PARTY_CREATE_SELF) ||
                styledText.matches(PARTY_JOIN_SELF);

        var matcher = styledText.getMatcher(PARTY_JOIN_OTHER);
        if (matcher.matches() && !shouldAuth) {
            var username = matcher.group(1);

            var player = MinecraftClient.getInstance().player;
            if (player != null) {
                shouldAuth = player.getGameProfile().getName().equals(username);
            }
        }

        if (shouldAuth) {
            WynntilsMod.info("Attempting to re-authenticate with Hades (party created/joined)");

            // Re-authenticate with the player server.
            Services.Hades.tryDisconnect();
            Services.WynntilsAccount.reauth();
            Models.Player.reset();
        }
    }
}
