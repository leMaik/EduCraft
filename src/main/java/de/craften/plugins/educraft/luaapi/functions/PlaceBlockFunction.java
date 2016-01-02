package de.craften.plugins.educraft.luaapi.functions;

import de.craften.plugins.educraft.luaapi.EduCraftApiFunction;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

/**
 * Lua API function that places a block where the entity stands and moves the entity one up.
 */
public class PlaceBlockFunction extends EduCraftApiFunction {
    @Override
    public Varargs execute(Varargs varargs) {
        Block block = getApi().getEntity().getEntity().getLocation().getBlock();

        if (getApi().getEnvironment().contains(block.getRelative(BlockFace.UP).getLocation())) {
            block.setType(getMaterial(varargs.checkjstring(1)));
            getApi().moveTo(getApi().getLocation().clone().add(0, 1, 0), false);
        }
        
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
