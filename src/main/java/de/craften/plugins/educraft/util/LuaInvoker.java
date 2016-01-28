package de.craften.plugins.educraft.util;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

/**
 * A caller for lua functions that may handle errors.
 */
public interface LuaInvoker {
    /**
     * Invokes the given function with the given arguments. Any exceptions that occurred while running the code are
     * rethrown.
     *
     * @param function function
     * @param args     arguments
     * @return result of the function
     * @throws org.luaj.vm2.LuaError if any error occurs
     */
    Varargs safeInvoke(LuaValue function, Varargs args);
}
