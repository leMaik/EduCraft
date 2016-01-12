package de.craften.plugins.educraft.luaapi.functions;

import de.craften.plugins.educraft.environment.LivingArmorStandBehavior;
import de.craften.plugins.educraft.luaapi.EduCraftApiFunction;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

/**
 * Lua API function to place a torch.
 */
public class PlaceTorchFunction extends EduCraftApiFunction {
    @Override
    protected void beforeExecute(Varargs varargs) {
        if (getApi().getInventory().hasItem(Material.TORCH, 1)) {
            LivingArmorStandBehavior armorStand = (LivingArmorStandBehavior) getApi().getEntity().getBehaviors(LivingArmorStandBehavior.class).iterator().next();
            armorStand.setItemInHand(new ItemStack(Material.TORCH));
        }
    }

    @Override
    public Varargs execute(Varargs varargs) {
        if (getApi().getInventory().hasItem(Material.TORCH, 1)) {
            Block currentBlock = getApi().getLocation().getBlock();
            if (currentBlock.getType() == Material.AIR) {
                getApi().getInventory().takeItems(Material.TORCH, 1);
                currentBlock.setType(Material.TORCH);
            }

            LivingArmorStandBehavior armorStand = (LivingArmorStandBehavior) getApi().getEntity().getBehaviors(LivingArmorStandBehavior.class).iterator().next();
            armorStand.setItemInHand(null);
        }

        return LuaValue.NIL;
    }
}
