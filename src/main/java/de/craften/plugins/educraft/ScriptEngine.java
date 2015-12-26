package de.craften.plugins.educraft;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
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
     * Sets all values from the given table as global variables.
     *
     * @param table table to installGlobal as global variables
     */
    public void mergeGlobal(LuaTable table) {
        for (LuaValue key : table.keys()) {
            globals.set(key, table.get(key));
        }
    }

    /**
     * Sets the given value as a global variable with the given name.
     *
     * @param name  name of the variable
     * @param value value to set the variable to
     */
    public void setGlobal(String name, LuaValue value) {
        globals.set(name, value);
    }
}