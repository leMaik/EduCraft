package de.craften.plugins.educraft.luaapi.functions;

import de.craften.plugins.educraft.luaapi.EduCraftApiFunction;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

/**
 * A function to assert things.
 */
public class AssertFunction extends EduCraftApiFunction {
    @Override
    public Varargs invoke(Varargs args) {
        if (!args.checkboolean(1)) {
            if (args.isnil(2)) {
                getApi().sendMessage("Assertion failed.");
                throw new LuaError("Assertion failed.");
            } else {
                getApi().sendMessage("Assertion failed: " + args.tojstring(2));
                throw new LuaError("Assertion failed: " + args.tojstring(2));
            }
        }
        return LuaValue.NIL;
    }

    @Override
    public Varargs execute(Varargs varargs) {
        return LuaValue.NIL; //no-op (this is not invoked)
    }
}
