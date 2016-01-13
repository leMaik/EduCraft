package de.craften.plugins.educraft.luaapi;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

/**
 * A table with information about the environment.
 */
public class EnvironmentTable extends LuaTable {
    private EduCraftApi api;

    public EnvironmentTable(EduCraftApi api) {
        this.api = api;
    }

    @Override
    public LuaValue rawget(LuaValue key) {
        if (key.isstring()) {
            switch (key.checkjstring()) {
                case "width":
                    return LuaValue.valueOf(api.getEnvironment().getWidth());
                case "height":
                    return LuaValue.valueOf(api.getEnvironment().getHeight());
                case "length":
                    return LuaValue.valueOf(api.getEnvironment().getLength());
                case "mode":
                    return LuaValue.valueOf(api.getEnvironment().isSurvivalMode() ? "survival" : "creative");
            }
        }
        return super.rawget(key);
    }

}
