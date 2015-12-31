package de.craften.plugins.educraft.util;

import de.craften.plugins.managedentities.behavior.StationaryBehavior;
import org.bukkit.Location;

/**
 * A {@link StationaryBehavior} that remembers the very first location and can be reset to it.
 */
public class ResetableStationaryBehavior extends StationaryBehavior {
    private final Location startLocation;

    public ResetableStationaryBehavior(Location location, boolean isTurningAllowed) {
        super(location, isTurningAllowed);
        startLocation = location.clone();
    }

    /**
     * Resets the entity to very first location.
     */
    public void reset() {
        setLocation(startLocation);
    }
}
