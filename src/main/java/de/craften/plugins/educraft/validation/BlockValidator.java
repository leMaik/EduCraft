package de.craften.plugins.educraft.validation;

import de.craften.plugins.educraft.environment.EduCraftEnvironment;
import de.craften.plugins.educraft.inventory.BotInventory;
import org.bukkit.Material;
import org.bukkit.util.Vector;

/**
 * A validator that checks if a block at a certain relative location has a certain type.
 */
public class BlockValidator implements ProgramValidator {
    private final Material material;
    private final Vector location;

    /**
     * Creates a new block validator.
     *
     * @param material material the block should have
     * @param location relative location of the block to check, relative to the bottom north-west corner of the environment
     */
    public BlockValidator(Material material, Vector location) {
        this.material = material;
        this.location = location;
    }

    @Override
    public boolean validate(EduCraftEnvironment environment, BotInventory inventory) {
        return environment.getLocation().add(location).getBlock().getType() == material;
    }
}
