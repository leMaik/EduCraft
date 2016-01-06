package de.craften.plugins.educraft.validation;

import de.craften.plugins.educraft.environment.EduCraftEnvironment;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

/**
 * A validator that checks if there is a sheared sheep at a certain location.
 */
public class DeadEntityValidator implements ProgramValidator {
    private final Vector location;

    /**
     * Creates a new sheared sheep validator.
     *
     * @param location relative location of the sheep to check, relative to the bottom north-west corner of the environment
     */
    public DeadEntityValidator(Vector location) {
        this.location = location;
    }

    @Override
    public boolean validate(EduCraftEnvironment environment) {
        Entity entity = environment.getEntityAtRelative(location);
        return entity != null && entity.isDead();
    }
}
