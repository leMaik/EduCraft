package de.craften.plugins.educraft.validation;

import de.craften.plugins.educraft.environment.EduCraftEnvironment;

/**
 * A validator that checks an environment for certain properties.
 */
public interface ProgramValidator {
    /**
     * Validates the given environment.
     *
     * @param environment environment
     * @return true if the validation succeeded, false if not
     */
    boolean validate(EduCraftEnvironment environment);
}
