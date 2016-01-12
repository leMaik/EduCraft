package de.craften.plugins.educraft.validation;

import de.craften.plugins.educraft.environment.EduCraftEnvironment;
import de.craften.plugins.educraft.inventory.BotInventory;
import org.bukkit.Material;

/**
 * A validator that checks the bot's inventory for certain items.
 */
public class InventoryValidator implements ProgramValidator {
    private final Material type;
    private final int amount;

    /**
     * Creates a new inventory validator.
     *
     * @param type   type of the item to check for
     * @param amount minimum required amount of that item
     */
    public InventoryValidator(Material type, int amount) {
        this.type = type;
        this.amount = amount;
    }

    @Override
    public boolean validate(EduCraftEnvironment environment, BotInventory inventory) {
        return inventory.hasItem(type, amount);
    }
}
