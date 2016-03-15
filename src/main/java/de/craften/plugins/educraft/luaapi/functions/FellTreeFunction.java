package de.craften.plugins.educraft.luaapi.functions;

import de.craften.plugins.educraft.environment.LivingArmorStandBehavior;
import de.craften.plugins.educraft.luaapi.EduCraftApiFunction;
import org.bukkit.Material;
import org.bukkit.TreeSpecies;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Tree;
import org.luaj.vm2.Varargs;

/**
 * A function to fell an entire tree at once.
 */
public class FellTreeFunction extends EduCraftApiFunction {
    @Override
    protected void beforeExecute(Varargs varargs) {
        LivingArmorStandBehavior armorStand = (LivingArmorStandBehavior) getApi().getEntity().getBehaviors(LivingArmorStandBehavior.class).iterator().next();
        armorStand.setItemInHand(new ItemStack(Material.DIAMOND_AXE));
    }

    @Override
    public Varargs execute(Varargs varargs) {
        Block block = getApi().getBlockAhead();
        if (block.getType() == Material.LOG || block.getType() == Material.LOG_2) {
            MaterialData data = block.getState().getData();
            if (data instanceof Tree) {
                getApi().getInventory().giveItem(block.getType(), fellTree(block, ((Tree) data).getSpecies()));
            }
        }
        return NIL;
    }

    @Override
    protected void afterExecute(Varargs varargs) {
        LivingArmorStandBehavior armorStand = (LivingArmorStandBehavior) getApi().getEntity().getBehaviors(LivingArmorStandBehavior.class).iterator().next();
        armorStand.setItemInHand(null);
    }

    private int fellTree(Block block, TreeSpecies species) {
        int count = 0;
        if (block.getType() == Material.LOG || block.getType() == Material.LOG_2) {
            count++;
        }
        block.setType(Material.AIR);

        for (BlockFace face : BlockFace.values()) {
            if (face != BlockFace.DOWN) {
                Block neighbor = block.getRelative(face);
                MaterialData data = neighbor.getState().getData();
                if (data instanceof Tree && ((Tree) data).getSpecies().equals(species)) {
                    count += fellTree(neighbor, species);
                }
            }
        }
        return count;
    }
}
