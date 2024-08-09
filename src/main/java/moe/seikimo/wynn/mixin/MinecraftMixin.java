package moe.seikimo.wynn.mixin;

import moe.seikimo.wynn.features.ClipboardCoordinates;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.client.util.Window;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MinecraftMixin {
    @Shadow public abstract Window getWindow();

    @Inject(method = "<init>", at = @At("TAIL"))
    public void onInitialize(RunArgs args, CallbackInfo ci) {
        ClipboardCoordinates.initialize(this.getWindow().getHandle());
    }
}
