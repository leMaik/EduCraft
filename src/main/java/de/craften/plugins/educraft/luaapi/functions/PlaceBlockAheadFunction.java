package de.craften.plugins.educraft.luaapi.functions;

import de.craften.plugins.educraft.environment.LivingArmorStandBehavior;
import org.bukkit.inventory.ItemStack;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

/**
 * Lua API function that places a block in front of the entity.
 */
public class PlaceBlockAheadFunction extends PlaceBlockFunction {
    @Override
    protected void beforeExecute(Varargs varargs) {
        LivingArmorStandBehavior armorStand = (LivingArmorStandBehavior) getApi().getEntity().getBehaviors(LivingArmorStandBehavior.class).iterator().next();
        armorStand.setItemInHand(new ItemStack(PlaceBlockFunction.getMaterial(varargs.checkjstring(1))));
    }

    @Override
    public Varargs execute(Varargs varargs) {
        return super.execute(LuaValue.varargsOf(new LuaValue[]{varargs.arg(1), LuaValue.ONE, LuaValue.ZERO}));
    }
}
