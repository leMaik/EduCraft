package de.craften.plugins.educraft.inventory;

import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;

/**
 * An inventory that only contains items that were previously added.
 */
public class SurvivalInventory implements BotInventory {
    private Map<Material, Integer> items = new HashMap<>();

    @Override
    public boolean hasItem(Material type, int count) {
        Integer amount = items.get(type);
        return amount != null && amount >= count;
    }

    @Override
    public void giveItem(Material type, int count) {
        Integer amount = items.get(type);
        if (amount == null) {
            items.put(type, count);
        } else {
            items.put(type, amount + count);
        }
    }

    @Override
    public boolean takeItems(Material type, int count) {
        Integer amount = items.get(type);
        if (amount != null) {
            if (amount > count) {
                items.put(type, amount - count);
                return true;
            } else if (amount == count) {
                items.remove(type);
                return true;
            }
        }
        return false;
    }
}
