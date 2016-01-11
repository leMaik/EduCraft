package de.craften.plugins.educraft.inventory;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * An inventory for a bot.
 */
public interface BotInventory {
    /**
     * Checks if this inventory contains the specified number of a certain item.
     *
     * @param type  item type
     * @param count number of items
     * @return true if this inventory contains the specified number of that item, false if not
     */
    boolean hasItem(Material type, int count);

    /**
     * Puts the given number of the given item type in this inventory.
     *
     * @param type  item type
     * @param count number of items
     */
    void giveItem(Material type, int count);

    /**
     * Takes the given amount of the given item out of the inventory. If the inventory doesn't contain the items,
     * this does nothing and returns false.
     *
     * @param type  item type
     * @param count number of items
     * @return true if the given amount was available and taken, false if not
     */
    boolean takeItems(Material type, int count);
}
