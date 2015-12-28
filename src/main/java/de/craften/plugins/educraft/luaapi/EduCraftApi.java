package de.craften.plugins.educraft.luaapi;

import de.craften.plugins.educraft.luaapi.functions.*;
import de.craften.plugins.managedentities.ManagedEntity;
import de.craften.plugins.managedentities.behavior.StationaryBehavior;
import org.bukkit.util.Vector;
import org.luaj.vm2.LuaTable;

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
        set("ifBlockAhead", new IfBlockAheadFunction().withApi(this));
        set("placeBlock", new PlaceBlockFunction().withApi(this));

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
}
