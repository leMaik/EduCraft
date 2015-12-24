package de.craften.plugins.educraft.luaapi;

import de.craften.plugins.managedentities.ManagedEntity;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ZeroArgFunction;

/**
 * The Lua API for EduCraft. This API works with an entity.
 */
public class EduCraftApi extends LuaTable {
    private final ManagedEntity entity;
    private Vector direction = new Vector(0, 0, -1);

    public EduCraftApi(final ManagedEntity entity) {
        this.entity = entity;
        entity.teleport(entity.getEntity().getLocation().setDirection(direction));

        set("moveForward", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                entity.teleport(entity.getEntity().getLocation().add(direction));
                return LuaValue.NIL;
            }
        });

        set("turnLeft", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                direction = new Vector(direction.getY(), 0, -direction.getX());
                entity.teleport(entity.getEntity().getLocation().setDirection(direction));
                return LuaValue.NIL;
            }
        });

        set("turnRight", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                direction = new Vector(-direction.getY(), 0, direction.getX());
                entity.teleport(entity.getEntity().getLocation().setDirection(direction));
                return LuaValue.NIL;
            }
        });

        set("placeTorch", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                Block blockInSight = entity.getEntity().getLocation().add(direction).getBlock();
                if (blockInSight.getType() == Material.AIR) {
                    blockInSight.setType(Material.TORCH);
                } else {
                    blockInSight = blockInSight.getRelative(BlockFace.UP);
                    if (blockInSight.getRelative(0, 1, 0).getType() == Material.AIR) {
                        blockInSight.setType(Material.TORCH);
                    }
                }
                return LuaValue.NIL;
            }
        });

        entity.teleport(entity.getEntity().getLocation().setDirection(direction));
    }
}
