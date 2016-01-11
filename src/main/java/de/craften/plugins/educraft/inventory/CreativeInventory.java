package de.craften.plugins.educraft.inventory;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

/**
 * A creative inventory that holds an infinite amount of any item.
 */
public class CreativeInventory implements BotInventory {
    @Override
    public boolean hasItem(Material type, int count) {return true;
    }

    @Override
    public void giveItem(Material type, int count) {
        //nothing to do
    }

    @Override
    public boolean takeItems(Material type, int count) {
        return true ;
    }
}
