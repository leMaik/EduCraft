package de.craften.plugins.educraft.luaapi.functions;

import de.craften.plugins.educraft.luaapi.EduCraftApiFunction;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

/**
 * A log function.
 */
public class LogFunction extends EduCraftApiFunction {
    @Override
    public Varargs invoke(Varargs varargs) {
        getApi().sendMessage("[LOG] " + varargs.tojstring(1));
        return LuaValue.NIL;
    }

    @Override
    public Varargs execute(Varargs varargs) {
        return LuaValue.NIL; //no-op (this is not invoked)
    }
}
