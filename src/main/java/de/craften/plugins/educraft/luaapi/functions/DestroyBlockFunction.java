package de.craften.plugins.educraft.luaapi.functions;

import de.craften.plugins.educraft.environment.LivingArmorStandBehavior;
import de.craften.plugins.educraft.luaapi.EduCraftApiFunction;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

/**
 * Lua API function to destroy the block in front of the entity.
 */
public class DestroyBlockFunction extends EduCraftApiFunction {
    @Override
    protected void beforeExecute(Varargs varargs) {
        LivingArmorStandBehavior armorStand = (LivingArmorStandBehavior) getApi().getEntity().getBehaviors(LivingArmorStandBehavior.class).iterator().next();
        armorStand.setItemInHand(new ItemStack(Material.DIAMOND_PICKAXE));
    }

    @Override
    public Varargs execute(Varargs varargs) {
        int forwardBackward = varargs.optint(1, 1);
        if (forwardBackward < 0 || forwardBackward > 1) {
            throw new LuaError("forward/backward offset must be either 0 or 1");
        }
        int upDown = varargs.optint(2, 0);
        if (upDown < -1 || upDown > 2) {
            throw new LuaError("up/down offset must be between -1 and 2");
        }

        Block block = getApi().getLocation().clone()
                .add(getApi().getDirection().clone().normalize().multiply(forwardBackward))
                .add(0, upDown, 0)
                .getBlock();

        if (getApi().getEnvironment().contains(block.getLocation())) {
            giveDrops(block);
            block.getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, block.getType());
            block.setType(Material.AIR);

            if (forwardBackward == 0 && upDown == -1) {
                //block below was destroyed => fall on solid block (without falling out of the environment)
                Block targetBlock = block;
                while (!targetBlock.getRelative(BlockFace.DOWN).getType().isSolid()
                        && getApi().getEnvironment().contains(targetBlock.getLocation().subtract(0, -1, 0))) {
                    targetBlock = targetBlock.getRelative(BlockFace.DOWN);
                }
                getApi().moveTo(targetBlock.getLocation(), false);
            }
        }

        return LuaValue.NIL;
    }

    private void giveDrops(Block block) {
        getApi().getInventory().giveItem(block.getType(), 1);
    }

    @Override
    protected void afterExecute(Varargs varargs) {
        LivingArmorStandBehavior armorStand = (LivingArmorStandBehavior) getApi().getEntity().getBehaviors(LivingArmorStandBehavior.class).iterator().next();
        armorStand.setItemInHand(null);
    }
}
