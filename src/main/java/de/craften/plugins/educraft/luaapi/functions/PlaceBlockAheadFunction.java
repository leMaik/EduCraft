package de.craften.plugins.educraft.luaapi.functions;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

/**
 * Lua API function that places a block in front of the entity.
 */
public class PlaceBlockAheadFunction extends PlaceBlockFunction {
    @Override
    public Varargs execute(Varargs varargs) {
        return super.execute(LuaValue.varargsOf(new LuaValue[]{varargs.arg(1), LuaValue.ONE, LuaValue.ZERO}));
    }
}
