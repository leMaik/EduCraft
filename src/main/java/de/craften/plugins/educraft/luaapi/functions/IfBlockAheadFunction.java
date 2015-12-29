package de.craften.plugins.educraft.luaapi.functions;

import de.craften.plugins.educraft.luaapi.EduCraftApiFunction;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

/**
 * Lua API function that checks the block ahead of the entity.
 * <p/>
 * The function takes two arguments: a string for the block
 * to check for ("", "lava" or "water") and a callback that is invoked if the check is positive.
 */
public class IfBlockAheadFunction extends EduCraftApiFunction {
    @Override
    public Varargs execute(Varargs varargs) {
        Block blockAhead = getApi().getBlockAhead();
        boolean isAhead;

        switch (varargs.checkjstring(1)) {
            case "":
                isAhead = blockAhead.isEmpty();
                break;
            case "lava":
                blockAhead = blockAhead.getRelative(BlockFace.DOWN);
                isAhead = blockAhead.getType() == Material.LAVA || blockAhead.getType() == Material.STATIONARY_LAVA;
                break;
            case "water":
                blockAhead = blockAhead.getRelative(BlockFace.DOWN);
                isAhead = blockAhead.getType() == Material.WATER || blockAhead.getType() == Material.STATIONARY_WATER;
                break;
            default:
                throw new LuaError("Unsupported block type, must be '', 'lava' or 'water'.");
        }

        if (isAhead) {
            varargs.checkfunction(2).invoke();
        }

        return LuaValue.NIL;
    }
}
