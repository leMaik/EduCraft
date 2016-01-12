package de.craften.plugins.educraft.validation;

import de.craften.plugins.educraft.environment.EduCraftEnvironment;
import de.craften.plugins.educraft.inventory.BotInventory;
import org.bukkit.Location;
import org.bukkit.util.Vector;

/**
 * A validator that checks if the bot (the programmable entity) of a program is at a certain position.
 */
public class BotLocationValidator implements ProgramValidator {
    private final Vector location;

    /**
     * Creates a new bot location validator.
     *
     * @param location location where the bot should be, relative to the bottom north-west corner of the environment
     */
    public BotLocationValidator(Vector location) {
        this.location = location;
    }

    @Override
    public boolean validate(EduCraftEnvironment environment, BotInventory inventory) {
        Location absoluteLocation = environment.getLocation().add(location);
        return environment.getEntity().getEntity().getLocation().getBlock().getLocation().equals(absoluteLocation);
    }
}
