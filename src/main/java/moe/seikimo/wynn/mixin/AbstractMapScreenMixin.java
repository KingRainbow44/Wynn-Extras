package moe.seikimo.wynn.mixin;

import com.wynntils.core.consumers.screens.WynntilsScreen;
import com.wynntils.screens.maps.AbstractMapScreen;
import moe.seikimo.wynn.features.PartySynchronize;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractMapScreen.class)
public abstract class AbstractMapScreenMixin extends WynntilsScreen {
    @Shadow(remap = false) protected float centerX;

    @Shadow(remap = false) protected float centerZ;

    @Shadow(remap = false) protected float zoomRenderScale;

    @Shadow(remap = false) protected float mapCenterX;

    @Shadow(remap = false) protected float mapCenterZ;

    protected AbstractMapScreenMixin(Text component) {
        super(component);
    }

    @Inject(method = "setCompassToMouseCoords", at = @At("TAIL"), remap = false)
    public void onSetCompassToMouseCoords(
            double mouseX, double mouseY,
            boolean removeAll, CallbackInfo ci) {
        // Convert mouse coordinates into Minecraft coordinates.
        var gameX = (mouseX - this.centerX) / this.zoomRenderScale + this.mapCenterX;
        var gameZ = (mouseY - this.centerZ) / this.zoomRenderScale + this.mapCenterZ;

        PartySynchronize.pingLocation(gameX, gameZ);
    }
}
