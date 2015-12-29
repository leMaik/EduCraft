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
        Block targetLocation = getApi().getBlockAhead();
        Collection<Entity> entities = getApi().getEntitiesAhead();

        if (!targetLocation.getType().isSolid()
                && !targetLocation.getRelative(BlockFace.UP).getType().isSolid()
                && entities.isEmpty()) {
            getApi().getStationaryBehavior().setLocation(targetLocation.getLocation().add(0.5, 0, 0.5));
        }

        return LuaValue.NIL;
    }
}
