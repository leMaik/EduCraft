package de.craften.plugins.educraft.luaapi.functions;

import de.craften.plugins.educraft.luaapi.EduCraftApiFunction;
import org.bukkit.Location;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

/**
 * Lua API function to move one block forward.
 */
public class MoveForwardFunction extends EduCraftApiFunction {
    @Override
    public Varargs execute(Varargs varargs) {
        Location targetLocation = getApi().getStationaryBehavior().getLocation().clone().add(getApi().getDirection());

        if (!targetLocation.getBlock().getType().isSolid()) {
            getApi().getStationaryBehavior().setLocation(targetLocation);
        }

        return LuaValue.NIL;
    }
}
