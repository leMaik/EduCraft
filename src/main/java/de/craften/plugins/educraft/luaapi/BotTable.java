package de.craften.plugins.educraft.luaapi;

import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;
import org.luaj.vm2.*;

/**
 * A table with information about the bot.
 */
public class BotTable extends LuaTable {
    private EduCraftApi api;

    public BotTable(EduCraftApi api) {
        this.api = api;
    }

    @Override
    public LuaValue rawget(LuaValue key) {
        if (key.isstring()) {
            switch (key.checkjstring()) {
                case "x":
                    return getX();
                case "y":
                    return getY();
                case "z":
                    return getZ();
                case "direction":
                    return getDirection();
            }
        }
        return super.rawget(key);
    }

    private LuaInteger getX() {
        return LuaValue.valueOf(api.getLocation().clone().subtract(api.getEnvironment().getLocation()).getBlockX());
    }

    private LuaInteger getY() {
        return LuaValue.valueOf(api.getLocation().clone().subtract(api.getEnvironment().getLocation()).getBlockY());
    }

    private LuaInteger getZ() {
        return LuaValue.valueOf(api.getLocation().clone().subtract(api.getEnvironment().getLocation()).getBlockZ());
    }

    private LuaString getDirection() {
        Vector d = api.getDirection();
        if (d.getBlockX() == BlockFace.NORTH.getModX() && d.getBlockZ() == BlockFace.NORTH.getModZ()) {
            return LuaValue.valueOf("north");
        } else if (d.getBlockX() == BlockFace.EAST.getModX() && d.getBlockZ() == BlockFace.EAST.getModZ()) {
            return LuaValue.valueOf("east");
        } else if (d.getBlockX() == BlockFace.SOUTH.getModX() && d.getBlockZ() == BlockFace.SOUTH.getModZ()) {
            return LuaValue.valueOf("south");
        } else if (d.getBlockX() == BlockFace.WEST.getModX() && d.getBlockZ() == BlockFace.WEST.getModZ()) {
            return LuaValue.valueOf("west");
        } else {
            throw new LuaError("Invalid bot direction.");
        }
    }
}
