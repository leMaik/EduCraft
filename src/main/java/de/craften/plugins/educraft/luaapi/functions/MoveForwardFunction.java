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
        Block targetBlock = getTargetBlock();
        Location targetLocation = targetBlock.getLocation();

        if (!targetBlock.getType().isSolid()
                && !targetBlock.getRelative(BlockFace.UP).getType().isSolid()
                && getApi().getEnvironment().getSheepAt(targetLocation) == null
                && getApi().getEnvironment().contains(targetLocation)) {
            getApi().moveTo(targetLocation, false);

            while (!targetBlock.getRelative(BlockFace.DOWN).getType().isSolid() && targetBlock.getY() > 0) {
                targetBlock = targetBlock.getRelative(BlockFace.DOWN);
            }
            getApi().moveTo(targetBlock.getLocation(), false);
        }

        return LuaValue.NIL;
    }

    /**
     * Gets the block this function will move the entity to.
     *
     * @return target block
     */
    protected Block getTargetBlock() {
        return getApi().getBlockAhead();
    }
}
