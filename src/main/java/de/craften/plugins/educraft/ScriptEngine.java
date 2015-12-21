package de.craften.plugins.educraft;

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
}