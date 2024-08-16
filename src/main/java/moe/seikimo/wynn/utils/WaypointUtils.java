package moe.seikimo.wynn.utils;

import com.wynntils.core.components.Managers;
import com.wynntils.features.map.MainMapFeature;
import com.wynntils.models.marker.MarkerModel;
import com.wynntils.services.map.pois.CustomPoi;
import com.wynntils.utils.colors.CustomColor;
import com.wynntils.utils.mc.type.Location;
import com.wynntils.utils.mc.type.PoiLocation;
import com.wynntils.utils.render.Texture;

public interface WaypointUtils {
    /**
     * @param location The location of the waypoint.
     * @return Whether a waypoint with the given location exists.
     */
    static boolean waypointExists(Location location) {
        return Managers.Feature
                .getFeatureInstance(MainMapFeature.class)
                .customPois.get().stream()
                .anyMatch(poi -> poi.getLocation()
                        .asLocation().equals(location));
    }

    /**
     * This creates a waypoint (a ping) at the given location.
     *
     * @param name The name of the waypoint.
     * @param location The location to ping.
     */
    static void pingLocation(String name, Location location) {
        var waypoints = MarkerModel.USER_WAYPOINTS_PROVIDER;
        waypoints.removeAllLocations();
        waypoints.addLocation(location, name);
    }

    /**
     * This creates a waypoint (a ping) at the given location.
     *
     * @param name The name of the waypoint.
     * @param location The location to ping.
     * @param texture The texture of the waypoint.
     */
    static void pingLocation(String name, Location location, Texture texture) {
        var waypoints = MarkerModel.USER_WAYPOINTS_PROVIDER;
        waypoints.removeAllLocations();
        waypoints.addLocation(location, texture, CustomColor.NONE, CustomColor.NONE, name);
    }

    /**
     * This doesn't actually create a waypoint, rather it creates a {@link PoiLocation}.
     */
    static void createWaypoint(String name, Location location) {
        var poi = new CustomPoi(
                new PoiLocation(location.x, location.y, location.z),
                name,
                CustomColor.NONE,
                Texture.FLAG,
                CustomPoi.Visibility.ALWAYS
        );

        var mainMap = Managers.Feature
                .getFeatureInstance(MainMapFeature.class);

        var config = mainMap.customPois;
        config.get().add(poi);
        config.touched();

        mainMap.updateWaypoints();
    }
}
