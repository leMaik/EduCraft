package de.craften.plugins.educraft.luaapi.functions;

import de.craften.plugins.educraft.luaapi.EduCraftApiFunction;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

/**
 * Lua API function to destroy the block in front of the entity.
 */
public class DestroyBlockFunction extends EduCraftApiFunction {
    @Override
    public Varargs execute(Varargs varargs) {
        if (getApi().getEnvironment().contains(getApi().getBlockAhead().getLocation())) {
            getApi().getBlockAhead().breakNaturally();
        }
        return LuaValue.NIL;
    }
}
