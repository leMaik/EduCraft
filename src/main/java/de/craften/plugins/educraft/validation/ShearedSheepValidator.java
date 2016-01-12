package de.craften.plugins.educraft.validation;

import de.craften.plugins.educraft.environment.EduCraftEnvironment;
import de.craften.plugins.educraft.inventory.BotInventory;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Sheep;
import org.bukkit.util.Vector;

/**
 * A validator that checks if there is a sheared sheep at a certain location.
 */
public class ShearedSheepValidator implements ProgramValidator {
    private final Vector location;

    /**
     * Creates a new sheared sheep validator.
     *
     * @param location relative location of the sheep to check, relative to the bottom north-west corner of the environment
     */
    public ShearedSheepValidator(Vector location) {
        this.location = location;
    }

    @Override
    public boolean validate(EduCraftEnvironment environment, BotInventory inventory) {
        Entity entity = environment.getEntityAtRelative(location);
        return entity instanceof Sheep && ((Sheep) entity).isSheared();
    }
}
