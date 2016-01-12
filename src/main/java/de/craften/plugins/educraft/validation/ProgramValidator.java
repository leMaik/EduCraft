package de.craften.plugins.educraft.validation;

import de.craften.plugins.educraft.environment.EduCraftEnvironment;
import de.craften.plugins.educraft.inventory.BotInventory;

/**
 * A validator that checks an environment for certain properties.
 */
public interface ProgramValidator {
    /**
     * Validates the given environment.
     *
     * @param environment environment
     * @param inventory   the bot's inventory
     * @return true if the validation succeeded, false if not
     */
    boolean validate(EduCraftEnvironment environment, BotInventory inventory);
}
