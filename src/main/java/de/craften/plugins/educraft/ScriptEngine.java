package de.craften.plugins.educraft;

import de.craften.plugins.educraft.luaapi.EduCraftApi;
import org.luaj.vm2.*;
import org.luaj.vm2.compiler.LuaC;

/**
 * The script engine for LuaCraft.
 */
public class ScriptEngine {
    private final Globals globals;

    public ScriptEngine() {
        globals = new Globals();
        LuaC.install(globals);
    }

    /**
     * Compiles the given code.
     *
     * @param code code to compile
     * @return compiled code
     * @throws LuaError if compilation fails
     */
    public LuaValue compile(String code) {
        return globals.load(code);
    }

    /**
     * Install all values from the given table as global variable.
     *
     * @param table table to installGlobal as global variables
     */
    public void installGlobal(LuaTable table) {
        for (LuaValue key : table.keys()) {
            globals.set(key, table.get(key));
        }
    }
}