package de.craften.plugins.educraft.luaapi.functions;

import de.craften.plugins.educraft.luaapi.EduCraftApiFunction;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Sheep;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.util.Collection;

/**
 * Lua API function to shear the sheap in front of the entity.
 */
public class ShearFunction extends EduCraftApiFunction {
    @Override
    public Varargs execute(Varargs varargs) {
        Block block = getApi().getEntity().getEntity().getLocation().clone().add(getApi().getDirection()).getBlock();
        Collection<Entity> entities = block.getWorld().getNearbyEntities(block.getLocation().add(0.5, 0.5, 0.5), 0.5, 0.5, 0.5);

        for (Entity entity : entities) {
            if (entity instanceof Sheep) {
                ((Sheep) entity).setSheared(true);
                break;
            }
        }

        return LuaValue.NIL;
    }
}
