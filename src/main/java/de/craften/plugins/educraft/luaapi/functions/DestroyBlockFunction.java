package de.craften.plugins.educraft.luaapi.functions;

import de.craften.plugins.educraft.environment.LivingArmorStandBehavior;
import de.craften.plugins.educraft.luaapi.EduCraftApiFunction;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

/**
 * Lua API function to destroy the block in front of the entity.
 */
public class DestroyBlockFunction extends EduCraftApiFunction {
    @Override
    public Varargs execute(Varargs varargs) {
        LivingArmorStandBehavior armorStand = (LivingArmorStandBehavior) getApi().getEntity().getBehaviors(LivingArmorStandBehavior.class).iterator().next();
        armorStand.setItemInHand(new ItemStack(Material.DIAMOND_PICKAXE));

        if (getApi().getEnvironment().contains(getApi().getBlockAhead().getLocation())) {
            getApi().getBlockAhead().setType(Material.AIR);
        }
        return LuaValue.NIL;
    }

    @Override
    protected void afterExecute() {
        LivingArmorStandBehavior armorStand = (LivingArmorStandBehavior) getApi().getEntity().getBehaviors(LivingArmorStandBehavior.class).iterator().next();
        armorStand.setItemInHand(null);
    }
}
