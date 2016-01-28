package de.craften.plugins.educraft;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.compiler.LuaC;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

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
     * @param code      code to compile
     * @param chunkName name of the created chunk (used in error messages)
     * @return compiled code
     * @throws LuaError if compilation fails
     */
    public LuaValue compile(String code, String chunkName) {
        return compile(new ByteArrayInputStream(code.getBytes(StandardCharsets.UTF_8)), chunkName);
    }

    /**
     * Compiles the code from the given input stream with the given chunk name.
     *
     * @param inputStream input stream of the code
     * @param chunkName   name of the created chunk (used in error messages)
     * @return compiled code
     */
    public LuaValue compile(InputStream inputStream, String chunkName) {
        return globals.load(new InputStreamReader(inputStream), chunkName);
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