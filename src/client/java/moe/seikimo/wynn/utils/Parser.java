package moe.seikimo.wynn.utils;

import net.minecraft.util.math.Vec3d;

import java.util.regex.Pattern;

public interface Parser {
    /**
     * This can read the following:
     * - [x, y, z]
     * - (x, y, z)
     * - x, y, z
     */
    Pattern COORDINATES = Pattern.compile("[\\[({]?\\s*(-?\\d+)\\s*,\\s*(-?\\d+)\\s*,\\s*(-?\\d+)\\s*[])}]?");

    /**
     * Parses a coordinates string.
     *
     * @param coordinates The coordinates string.
     * @return The parsed coordinates.
     */
    static Vec3d parseCoordinates(String coordinates) {
        var matcher = COORDINATES.matcher(coordinates.trim());
        if (matcher.matches() && matcher.groupCount() == 3) {
            var x = Double.parseDouble(matcher.group(1));
            var y = Double.parseDouble(matcher.group(2));
            var z = Double.parseDouble(matcher.group(3));
            return new Vec3d(x, y, z);
        }
        return null;
    }
}
