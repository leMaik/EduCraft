package de.craften.plugins.educraft.luaapi.functions;

import de.craften.plugins.educraft.luaapi.EduCraftApiFunction;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

/**
 * Lua API function to plant crops.
 */
public class PlantCropFunction extends EduCraftApiFunction {
    @Override
    public Varargs execute(Varargs varargs) {
        Block currentBlock = getApi().getLocation().getBlock();
        Block blockBelow = currentBlock.getRelative(BlockFace.DOWN);

        if (currentBlock.getType() == Material.AIR && blockBelow.getType() == Material.SOIL) {
            currentBlock.setType(Material.CROPS);
        }

        return LuaValue.NIL;
    }
}
