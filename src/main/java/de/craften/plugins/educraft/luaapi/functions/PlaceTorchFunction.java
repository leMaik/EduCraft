package de.craften.plugins.educraft.luaapi.functions;

import de.craften.plugins.educraft.luaapi.EduCraftApiFunction;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

/**
 * Lua API function to place a torch.
 */
public class PlaceTorchFunction extends EduCraftApiFunction {
    @Override
    public Varargs execute(Varargs varargs) {
        Block currentBlock = getApi().getEntity().getEntity().getLocation().getBlock();

        if (currentBlock.getType() == Material.AIR) {
            currentBlock.setType(Material.TORCH);
        }

        return LuaValue.NIL;
    }
}
