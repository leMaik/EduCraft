package de.craften.plugins.educraft.luaapi.functions;

import de.craften.plugins.educraft.environment.LivingArmorStandBehavior;
import de.craften.plugins.educraft.luaapi.EduCraftApiFunction;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

/**
 * Lua API function that places a block where the entity stands, adding an optional offset. Moves the entity one up, if
 * the block is placed on its position.
 */
public class PlaceBlockFunction extends EduCraftApiFunction {
    @Override
    protected void beforeExecute(Varargs varargs) {
        LivingArmorStandBehavior armorStand = (LivingArmorStandBehavior) getApi().getEntity().getBehaviors(LivingArmorStandBehavior.class).iterator().next();
        armorStand.setItemInHand(new ItemStack(getMaterial(varargs.checkjstring(1))));
    }

    @Override
    public Varargs execute(Varargs varargs) {
        int forwardBackward = varargs.optint(2, 0);
        if (forwardBackward < -1 || forwardBackward > 1) {
            throw new LuaError("forward/backward offset must be between -1 and 1");
        }
        int upDown = varargs.optint(3, 0);
        if (upDown < -1 || upDown > 2) {
            throw new LuaError("up/down offset must be between -1 and 2");
        }

        Block block = getApi().getLocation().clone()
                .add(getApi().getDirection().clone().normalize().multiply(forwardBackward))
                .add(0, upDown, 0)
                .getBlock();

        if (forwardBackward == 0 && upDown == 0) {
            if (getApi().getEnvironment().contains(block.getRelative(BlockFace.UP).getLocation())) {
                Material material = getMaterial(varargs.checkjstring(1));
                block.setType(material);
                if (material.isSolid()) {
                    getApi().moveTo(block.getLocation().add(0, 1, 0), false);
                }
            }
        } else if (getApi().getEnvironment().contains(block.getLocation()) && !block.getType().isSolid()) {
            block.setType(getMaterial(varargs.checkjstring(1)));
        }

        LivingArmorStandBehavior armorStand = (LivingArmorStandBehavior) getApi().getEntity().getBehaviors(LivingArmorStandBehavior.class).iterator().next();
        armorStand.setItemInHand(null);

        return LuaValue.NIL;
    }

    public static Material getMaterial(String name) {
        //missing: "dirtCoarse", "farmlandWet", "logAcacia", "logBirch", "logJungle", "logOak", "logSpruce",
        //         "planksAcacia", "planksBirch", "planksJungle", "planksOak", "planksSpruce", "tree", "wool"
        switch (name) {
            case "bedrock":
                return Material.BEDROCK;
            case "bricks":
                return Material.BRICK;
            case "oreCoal":
                return Material.COAL_ORE;
            case "dirt":
                return Material.DIRT;
            case "oreEmerald":
                return Material.EMERALD_ORE;
            case "farmland":
                return Material.SOIL;
            case "glass":
                return Material.GLASS;
            case "oreGold":
                return Material.GOLD_ORE;
            case "grass":
                return Material.GRASS;
            case "gravel":
                return Material.GRAVEL;
            case "clayHardened":
                return Material.HARD_CLAY;
            case "oreIron":
                return Material.IRON_ORE;
            case "oreLapis":
                return Material.LAPIS_ORE;
            case "lava":
                return Material.LAVA;
            case "logAcacia":
                return Material.LOG;
            case "oreRedstone":
                return Material.REDSTONE_ORE;
            case "sand":
                return Material.SAND;
            case "sandstone":
                return Material.SANDSTONE;
            case "stone":
                return Material.STONE;
            case "tnt":
                return Material.TNT;
            case "wool":
                return Material.WOOL;
            default:
                throw new LuaError("Unsupported block type: " + name);
        }
    }
}
