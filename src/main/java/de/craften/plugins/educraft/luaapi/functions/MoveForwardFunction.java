package de.craften.plugins.educraft.luaapi.functions;

import de.craften.plugins.educraft.luaapi.EduCraftApiFunction;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

/**
 * Lua API function to move one block forward.
 */
public class MoveForwardFunction extends EduCraftApiFunction {
    @Override
    public Varargs execute(Varargs varargs) {
        Block blockAhead = getApi().getBlockAhead();
        Location locationAhead = blockAhead.getLocation();

        if (!blockAhead.getType().isSolid()
                && !blockAhead.getRelative(BlockFace.UP).getType().isSolid()
                && getApi().getEnvironment().getSheepAt(locationAhead) == null
                && getApi().getEnvironment().contains(locationAhead)) {
            getApi().moveTo(locationAhead, false);
        }

        while (!blockAhead.getRelative(BlockFace.DOWN).getType().isSolid() && blockAhead.getY() > 0) {
            blockAhead = blockAhead.getRelative(BlockFace.DOWN);
        }
        getApi().moveTo(blockAhead.getLocation(), false);

        return LuaValue.NIL;
    }
}
