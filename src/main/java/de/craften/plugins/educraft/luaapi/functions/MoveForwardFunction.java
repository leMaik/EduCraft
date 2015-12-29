package de.craften.plugins.educraft.luaapi.functions;

import de.craften.plugins.educraft.luaapi.EduCraftApiFunction;
import org.bukkit.Location;
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
        Location targetLocation = getApi().getStationaryBehavior().getLocation().clone().add(getApi().getDirection());
        Collection<Entity> entities = targetLocation.getWorld().getNearbyEntities(targetLocation.getBlock().getLocation().add(0.5, 0.5, 0.5), 0.5, 0.5, 0.5);

        if (!targetLocation.getBlock().getType().isSolid()
                && !targetLocation.getBlock().getRelative(BlockFace.UP).getType().isSolid()
                && entities.isEmpty()) {
            getApi().getStationaryBehavior().setLocation(targetLocation);
        }

        return LuaValue.NIL;
    }
}
