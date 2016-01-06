package de.craften.plugins.educraft.luaapi.functions;

import de.craften.plugins.educraft.environment.LivingArmorStandBehavior;
import de.craften.plugins.educraft.luaapi.EduCraftApiFunction;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

/**
 * Lua API function that places a block in front of the entity.
 */
public class PlaceBlockAheadFunction extends EduCraftApiFunction {
    @Override
    public Varargs execute(Varargs varargs) {
        Location location = getApi().getBlockAhead().getLocation();
        Material material = PlaceBlockFunction.getMaterial(varargs.checkjstring(1));

        LivingArmorStandBehavior armorStand = (LivingArmorStandBehavior) getApi().getEntity().getBehaviors(LivingArmorStandBehavior.class).iterator().next();
        armorStand.setItemInHand(new ItemStack(material));

        if (getApi().getEnvironment().contains(location) && !getApi().getEnvironment().isAliveEntityAt(location)) {
            getApi().getBlockAhead().setType(material);
        }

        return LuaValue.NIL;
    }

    @Override
    protected void afterExecute() {
        LivingArmorStandBehavior armorStand = (LivingArmorStandBehavior) getApi().getEntity().getBehaviors(LivingArmorStandBehavior.class).iterator().next();
        armorStand.setItemInHand(null);
    }
}
