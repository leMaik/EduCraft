package de.craften.plugins.educraft.luaapi.functions;

import de.craften.plugins.educraft.environment.LivingArmorStandBehavior;
import de.craften.plugins.educraft.luaapi.EduCraftApiFunction;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Sheep;
import org.bukkit.inventory.ItemStack;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

/**
 * Lua API function to shear the sheap in front of the entity.
 */
public class ShearFunction extends EduCraftApiFunction {
    @Override
    public Varargs execute(Varargs varargs) {
        LivingArmorStandBehavior armorStand = (LivingArmorStandBehavior) getApi().getEntity().getBehaviors(LivingArmorStandBehavior.class).iterator().next();
        armorStand.setItemInHand(new ItemStack(Material.SHEARS));

        for (Entity entity : getApi().getEntitiesAhead()) {
            if (entity instanceof Sheep) {
                ((Sheep) entity).setSheared(true);
                break;
            }
        }

        return LuaValue.NIL;
    }

    @Override
    protected void afterExecute() {
        LivingArmorStandBehavior armorStand = (LivingArmorStandBehavior) getApi().getEntity().getBehaviors(LivingArmorStandBehavior.class).iterator().next();
        armorStand.setItemInHand(null);
    }
}
