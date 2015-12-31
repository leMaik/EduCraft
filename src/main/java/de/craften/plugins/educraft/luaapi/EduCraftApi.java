package de.craften.plugins.educraft.luaapi;

import de.craften.plugins.educraft.luaapi.functions.*;
import de.craften.plugins.managedentities.ManagedEntity;
import de.craften.plugins.managedentities.behavior.StationaryBehavior;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
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

    public EduCraftApi(final ManagedEntity entity, BlockFace initialDirection) {
        this.entity = entity;
        stationary = (StationaryBehavior) entity.getBehaviors(StationaryBehavior.class).iterator().next();

        switch (initialDirection) {
            case NORTH:
                setDirection(new Vector(0, 0, -1));
                break;
            case EAST:
                setDirection(new Vector(1, 0, 0));
                break;
            case SOUTH:
                setDirection(new Vector(0, 0, 1));
                break;
            case WEST:
                setDirection(new Vector(-1, 0, 0));
                break;
            default:
                throw new IllegalArgumentException("Initial direction must be north, east, south or west");
        }

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

    /**
     * Gets the direction of the entity.
     *
     * @return direction
     */
    public Vector getDirection() {
        return direction;
    }

    /**
     * Sets the direction and rotates the entity.
     *
     * @param direction direction
     */
    public void setDirection(Vector direction) {
        this.direction = direction;

        Location location = getLocation().clone().setDirection(direction);
        moveTo(location);
    }

    /**
     * Gets the location of the entity.
     *
     * @return location of the entity
     */
    public Location getLocation() {
        return stationary.getLocation();
    }

    /**
     * Moves the entity to the given location (centered on the block).
     *
     * @param location location to teleport the entity to
     */
    public void moveTo(Location location) {
        Location blockLocation = new Location(location.getWorld(),
                location.getBlockX(), location.getBlockY(), location.getBlockZ(),
                stationary.getLocation().getYaw(), stationary.getLocation().getPitch());
        stationary.setLocation(blockLocation.add(0.5, 0, 0.5));
    }

    /**
     * Gets the block ahead of the entity.
     *
     * @return block ahead of the entity
     */
    public Block getBlockAhead() {
        return stationary.getLocation().clone().add(getDirection()).getBlock();
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
