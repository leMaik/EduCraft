package de.craften.plugins.educraft.luaapi;

import de.craften.plugins.educraft.luaapi.functions.*;
import de.craften.plugins.managedentities.ManagedEntity;
import de.craften.plugins.managedentities.behavior.StationaryBehavior;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;
import org.luaj.vm2.LuaTable;

import java.util.Collection;

/**
 * The Lua API for EduCraft. This API works with an entity.
 */
public class EduCraftApi extends LuaTable {
    private final ManagedEntity entity;
    private final StationaryBehavior stationary;
    private Vector direction = new Vector(0, 0, -1);

    public EduCraftApi(final ManagedEntity entity) {
        this.entity = entity;
        entity.spawn();

        stationary = new StationaryBehavior(entity.getEntity().getLocation().setDirection(direction), false);
        entity.addBehavior(stationary);

        set("moveForward", new MoveForwardFunction().withApi(this));
        set("turnLeft", new TurnLeftFunction().withApi(this));
        set("turnRight", new TurnRightFunction().withApi(this));
        set("placeTorch", new PlaceTorchFunction().withApi(this));
        set("plantCrop", new PlantCropFunction().withApi(this));
        set("ifBlockAhead", new IfBlockAheadFunction().withApi(this));
        set("placeBlock", new PlaceBlockFunction().withApi(this));
        set("placeBlockAhead", new PlaceBlockAheadFunction().withApi(this));
        set("shear", new ShearFunction().withApi(this));

        stationary.setLocation(stationary.getLocation().clone().setDirection(direction));
    }

    public ManagedEntity getEntity() {
        return entity;
    }

    public StationaryBehavior getStationaryBehavior() {
        return stationary;
    }

    public Vector getDirection() {
        return direction;
    }

    public void setDirection(Vector direction) {
        this.direction = direction;
    }

    /**
     * Gets the block ahead of the entity.
     *
     * @return block ahead of the entity
     */
    public Block getBlockAhead() {
        return getStationaryBehavior().getLocation().clone().add(getDirection()).getBlock();
    }

    /**
     * Gets all entities that are on the block ahead of the entity.
     *
     * @return all entities that are on the block ahead of the entity
     */
    public Collection<Entity> getEntitiesAhead() {
        Block blockAhead = getBlockAhead();
        return blockAhead.getWorld().getNearbyEntities(getBlockAhead().getLocation().add(0.5, 0.5, 0.5), 0.5, 0.5, 0.5);
    }
}
