package moe.seikimo.wynn.mixin;

import com.wynntils.models.marker.UserWaypointMarkerProvider;
import com.wynntils.screens.maps.AbstractMapScreen;
import com.wynntils.screens.maps.MainMapScreen;
import com.wynntils.utils.colors.CustomColor;
import com.wynntils.utils.mc.type.Location;
import com.wynntils.utils.render.Texture;
import moe.seikimo.wynn.features.PartySynchronize;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MainMapScreen.class)
public abstract class MainMapScreenMixin extends AbstractMapScreen {
    @Redirect(
            method = "doMouseClicked",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/wynntils/models/marker/UserWaypointMarkerProvider;addLocation(Lcom/wynntils/utils/mc/type/Location;Lcom/wynntils/utils/render/Texture;Ljava/lang/String;)V"
            ),
            remap = false
    )
    public void addWaypointLocation(
            UserWaypointMarkerProvider instance, Location location, Texture texture, String additionalText) {
        instance.addLocation(location, texture, additionalText);

        // At this point, we also want to update the pinged location.
        PartySynchronize.pingLocation(location.x, location.z, texture, additionalText);
    }
}
