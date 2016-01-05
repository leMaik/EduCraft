package de.craften.plugins.educraft.luaapi;

import de.craften.plugins.educraft.environment.EduCraftEnvironment;
import de.craften.plugins.educraft.luaapi.functions.*;
import de.craften.plugins.educraft.util.ResetableStationaryBehavior;
import de.craften.plugins.managedentities.ManagedEntity;
import de.craften.plugins.managedentities.behavior.StationaryBehavior;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;
import org.luaj.vm2.LuaTable;

import java.util.Collection;

/**
 * The Lua API for EduCraft. This API works with an entity.
 */
public class EduCraftApi extends LuaTable {
    private final EduCraftEnvironment environment;
    private final long functionDelay;
    private final StationaryBehavior stationary;
    private Vector direction = new Vector(0, 0, -1);

    public EduCraftApi(EduCraftEnvironment environment, long functionDelay) {
        this.environment = environment;
        this.functionDelay = functionDelay;
        stationary = (StationaryBehavior) environment.getEntity().getBehaviors(ResetableStationaryBehavior.class).iterator().next();
        setDirection(new Vector(environment.getStartDirection().getModX(), 0, environment.getStartDirection().getModZ()));

        set("moveForward", new MoveForwardFunction().withApi(this));
        set("turnLeft", new TurnLeftFunction().withApi(this));
        set("turnRight", new TurnRightFunction().withApi(this));
        set("jump", new JumpFunction().withApi(this));
        set("placeTorch", new PlaceTorchFunction().withApi(this));
        set("plantCrop", new PlantCropFunction().withApi(this));
        set("ifBlockAhead", new IfBlockAheadFunction().withApi(this));
        set("placeBlock", new PlaceBlockFunction().withApi(this));
        set("placeBlockAhead", new PlaceBlockAheadFunction().withApi(this));
        set("destroyBlock", new DestroyBlockFunction().withApi(this));
        set("shear", new ShearFunction().withApi(this));
        set("wait", new WaitFunction().withApi(this));

        stationary.setLocation(stationary.getLocation().clone().setDirection(direction));
    }

    public EduCraftEnvironment getEnvironment() {
        return environment;
    }

    public ManagedEntity getEntity() {
        return environment.getEntity();
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
        moveTo(location, true);
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
    public void moveTo(Location location, boolean turn) {
        Location blockLocation = new Location(location.getWorld(),
                location.getBlockX(), location.getBlockY(), location.getBlockZ(),
                turn ? location.getYaw() : stationary.getLocation().getYaw(),
                turn ? location.getPitch() : stationary.getLocation().getPitch());
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

    /**
     * Gets the delay between two functions.
     *
     * @return delay between two functions, in milliseconds
     */
    public long getFunctionDelay() {
        return functionDelay;
    }
}
