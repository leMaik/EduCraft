package de.craften.plugins.educraft.luaapi.functions;

import de.craften.plugins.educraft.environment.LivingArmorStandBehavior;
import de.craften.plugins.educraft.luaapi.EduCraftApiFunction;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

/**
 * Lua API function to plant crops.
 */
public class PlantCropFunction extends EduCraftApiFunction {
    @Override
    protected void beforeExecute(Varargs varargs) {
        LivingArmorStandBehavior armorStand = (LivingArmorStandBehavior) getApi().getEntity().getBehaviors(LivingArmorStandBehavior.class).iterator().next();
        armorStand.setItemInHand(new ItemStack(Material.CROPS));
    }

    @Override
    public Varargs execute(Varargs varargs) {
        Block currentBlock = getApi().getLocation().getBlock();
        Block blockBelow = currentBlock.getRelative(BlockFace.DOWN);

        if (currentBlock.getType() == Material.AIR && blockBelow.getType() == Material.SOIL) {
            currentBlock.setType(Material.CROPS);
        }

        LivingArmorStandBehavior armorStand = (LivingArmorStandBehavior) getApi().getEntity().getBehaviors(LivingArmorStandBehavior.class).iterator().next();
        armorStand.setItemInHand(null);

        return LuaValue.NIL;
    }
}
