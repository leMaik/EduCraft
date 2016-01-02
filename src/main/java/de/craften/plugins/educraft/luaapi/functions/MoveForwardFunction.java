package de.craften.plugins.educraft.luaapi.functions;

import de.craften.plugins.educraft.luaapi.EduCraftApiFunction;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.util.Collection;

/**
 * Lua API function to move one block forward.
 */
public class MoveForwardFunction extends EduCraftApiFunction {
    @Override
    public Varargs execute(Varargs varargs) {
        Block blockAhead = getApi().getBlockAhead();
        Collection<Entity> entities = getApi().getEntitiesAhead();

        if (!blockAhead.getType().isSolid()
                && !blockAhead.getRelative(BlockFace.UP).getType().isSolid()
                && getApi().getEnvironment().getSheepAt(blockAhead.getLocation()) == null) {
            getApi().moveTo(blockAhead.getLocation());
        }

        return LuaValue.NIL;
    }
}
