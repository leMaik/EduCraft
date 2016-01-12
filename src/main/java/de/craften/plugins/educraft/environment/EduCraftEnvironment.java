package de.craften.plugins.educraft.environment;

import de.craften.plugins.educraft.EduCraft;
import de.craften.plugins.educraft.util.ResetableStationaryBehavior;
import de.craften.plugins.educraft.validation.*;
import de.craften.plugins.managedentities.EntityManager;
import de.craften.plugins.managedentities.ManagedEntity;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.util.Vector;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;

/**
 * An in-game environment that programs can be run in.
 */
public class EduCraftEnvironment {
    private final Schematic schematic;
    private final Location location;
    private final Collection<ProgramValidator> validators;
    private final boolean survivalMode;

    private ManagedEntity entity;
    private Collection<ManagedEntity> entities = new ArrayList<>();
    private BlockFace startDirection;
    private UUID lockedBy;


    public EduCraftEnvironment(ConfigurationSection config) throws IOException {
        schematic = new Schematic(Paths.get(
                EduCraft.getPlugin(EduCraft.class).getDataFolder().getPath(), "levels",
                config.getString("schematic")).toFile());

        location = new Location(
                Bukkit.getWorld(config.getString("location.world")),
                config.getInt("location.x"),
                config.getInt("location.y"),
                config.getInt("location.z"));

        validators = new ArrayList<>();
        for (Map validation : config.getMapList("validate")) {
            if (validation.containsKey("assert")) {
                String locationComponents[] = validation.get("at").toString().split(",");
                Vector location = new Vector(Integer.parseInt(locationComponents[0].trim()),
                        Integer.parseInt(locationComponents[1].trim()),
                        Integer.parseInt(locationComponents[2].trim()));

                switch (validation.get("assert").toString().toLowerCase()) {
                    case "block":
                        validators.add(new BlockValidator(Material.matchMaterial(validation.get("is").toString()), location));
                        break;
                    case "bot":
                        validators.add(new BotLocationValidator(location));
                        break;
                    case "shearedsheep":
                    case "sheared_sheep":
                        validators.add(new ShearedSheepValidator(location));
                        break;
                    case "deadentity":
                    case "dead_entity":
                        validators.add(new DeadEntityValidator(location));
                        break;
                }
            }
        }

        survivalMode = config.getBoolean("survival", false);

        initialize();
    }

    private void initialize() {
        schematic.restoreAt(location);

        EntityManager entityManager = EduCraft.getPlugin(EduCraft.class).getEntityManager();

        for (int x = 0; x < schematic.getWidth(); x++) {
            for (int y = 0; y < schematic.getHeight(); y++) {
                for (int z = 0; z < schematic.getLength(); z++) {
                    Block block = location.getWorld().getBlockAt(
                            location.getBlockX() + x,
                            location.getBlockY() + y,
                            location.getBlockZ() + z);

                    if (block.getType() == Material.SIGN_POST || block.getType() == Material.WALL_SIGN) {
                        Sign sign = (Sign) block.getState();
                        if (sign.getLine(0).equalsIgnoreCase("[EduCraft]")) {
                            if (entity == null && sign.getLine(1).equalsIgnoreCase("start")) {
                                startDirection = ((org.bukkit.material.Sign) sign.getData()).getFacing();
                                Location startLocation = block.getLocation().add(0.5, 0, 0.5)
                                        .setDirection(new Vector(startDirection.getModX(), 0, startDirection.getModZ()));
                                entity = entityManager.spawn(startLocation, ArmorStand.class);
                                entity.addBehavior(new ResetableStationaryBehavior(startLocation, false));
                                entity.addBehavior(new LivingArmorStandBehavior());
                                entity.spawn();
                                block.setType(Material.AIR);
                            } else if (sign.getLine(1).equalsIgnoreCase("sheep")) {
                                ManagedEntity<Sheep> sheep = entityManager.spawn(block.getLocation().add(0.5, 0, 0.5), Sheep.class);
                                sheep.addBehavior(new ResetableStationaryBehavior(block.getLocation().add(0.5, 0, 0.5), false));
                                this.entities.add(sheep);
                                sheep.spawn();
                                block.setType(Material.AIR);
                            }
                        }
                    }
                }
            }
        }

        if (entity == null) {
            throw new IllegalArgumentException("No start location configured.");
        }
    }

    /**
     * Resets this environment.
     *
     * @param respawn true if the entities should be respawned, false if they should only be removed
     */
    public void reset(boolean respawn) {
        removeEntities();

        schematic.restoreAt(location);
        for (int x = 0; x < schematic.getWidth(); x++) {
            for (int y = 0; y < schematic.getHeight(); y++) {
                for (int z = 0; z < schematic.getLength(); z++) {
                    Block block = location.getWorld().getBlockAt(
                            location.getBlockX() + x,
                            location.getBlockY() + y,
                            location.getBlockZ() + z);

                    if (block.getType() == Material.SIGN_POST || block.getType() == Material.WALL_SIGN) {
                        Sign sign = (Sign) block.getState();
                        if (sign.getLine(0).equalsIgnoreCase("[EduCraft]")) {
                            if (sign.getLine(1).equalsIgnoreCase("start")) {
                                block.setType(Material.AIR);
                            } else if (sign.getLine(1).equalsIgnoreCase("sheep")) {
                                block.setType(Material.AIR);
                            }
                        }
                    }
                }
            }
        }

        if (respawn) {
            ((ResetableStationaryBehavior) entity.getBehaviors(ResetableStationaryBehavior.class).iterator().next()).reset();
            ((LivingArmorStandBehavior) entity.getBehaviors(LivingArmorStandBehavior.class).iterator().next()).setItemInHand(null);
            entity.spawn();
            for (ManagedEntity entity : entities) {
                ((ResetableStationaryBehavior) entity.getBehaviors(ResetableStationaryBehavior.class).iterator().next()).reset();
                entity.spawn();
            }
        }
    }

    /**
     * Checks if this environment fulfills all requirements of this environment and thus the program was successful.
     *
     * @return true if the program was successfull, false if not
     */
    public boolean fulfillsRequirements() {
        for (ProgramValidator validator : validators) {
            if (!validator.validate(this)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Removes the entities of this environment (the programmable entity and all requisite entities).
     */
    public void removeEntities() {
        entity.remove();
        for (ManagedEntity entity : entities) {
            entity.remove();
        }
    }

    public ManagedEntity getEntity() {
        return entity;
    }

    public Entity getEntityAtRelative(Vector location) {
        return getEntityAt(getLocation().add(location));
    }

    /**
     * Gets the entity at the given location. Note that this may also return dead entities that were killed while
     * running the program.
     *
     * @param location location
     * @return the entity at the given location or null if there is no entity at the given location
     */
    public Entity getEntityAt(Location location) {
        for (ManagedEntity sheep : this.entities) {
            Location entityLocation = sheep.getEntity().getLocation();
            if (entityLocation.getBlockX() == location.getBlockX()
                    && entityLocation.getBlockY() == location.getBlockY()
                    && entityLocation.getBlockZ() == location.getBlockZ()) {
                return sheep.getEntity();
            }
        }
        return null;
    }

    /**
     * Checks if a living entity is at the given location.
     *
     * @param location location to check
     * @return true if a living entity is at the given location, false if not
     */
    public boolean isAliveEntityAt(Location location) {
        Entity entity = getEntityAt(location);
        return entity != null && !entity.isDead();
    }

    public BlockFace getStartDirection() {
        return startDirection;
    }

    /**
     * Gets a copy of the location of this environment (the bottom north-west block).
     *
     * @return copy of the location of this environment
     */
    public Location getLocation() {
        return location.clone();
    }

    /**
     * Checks if the given location is inside this environment (based on the size of the schematic).
     *
     * @param location location to check
     * @return true if the given location is inside the environment, false if not
     */
    public boolean contains(Location location) {
        return getLocation().getWorld().equals(location.getWorld())

                && location.getX() >= getLocation().getX()
                && location.getX() < getLocation().getX() + schematic.getWidth()

                && location.getY() >= getLocation().getY()
                && location.getY() < getLocation().getY() + schematic.getHeight()

                && location.getZ() >= getLocation().getZ()
                && location.getZ() < getLocation().getZ() + schematic.getLength();
    }

    /**
     * Checks if  this environment is locked.
     *
     * @return true if this environment is locked, false if not
     */
    public boolean isLocked() {
        return lockedBy != null;
    }

    /**
     * Locks this environment.
     *
     * @param player player that locks this environment
     */
    public void lock(Player player) {
        lockedBy = player.getUniqueId();
    }

    /**
     * Unlocks this environment.
     */
    public void unlock() {
        lockedBy = null;
    }

    /**
     * Checks if this environment is in survival mode.
     *
     * @return true if this environment is in survival mode, false if not
     */
    public boolean isSurvivalMode() {
        return survivalMode;
    }
}
