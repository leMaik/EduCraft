package de.craften.plugins.educraft.luaapi.functions;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

/**
 * Lua API function to jump (move one block forward and one block up).
 */
public class JumpFunction extends MoveForwardFunction {
    @Override
    protected Block getTargetBlock() {
        return getApi().getBlockAhead().getRelative(BlockFace.UP);
    }
}
