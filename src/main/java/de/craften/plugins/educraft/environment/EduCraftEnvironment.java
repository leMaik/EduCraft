package de.craften.plugins.educraft.environment;

import de.craften.plugins.educraft.EduCraft;
import de.craften.plugins.educraft.util.ResetableStationaryBehavior;
import de.craften.plugins.managedentities.EntityManager;
import de.craften.plugins.managedentities.ManagedEntity;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Villager;
import org.bukkit.util.Vector;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;

/**
 * An in-game environment that programs can be run in.
 */
public class EduCraftEnvironment {
    private final Schematic schematic;
    private final Location location;

    private ManagedEntity entity;
    private Collection<ManagedEntity> sheep = new ArrayList<>();
    private BlockFace startDirection;

    public EduCraftEnvironment(ConfigurationSection config) throws IOException {
        schematic = new Schematic(Paths.get(
                EduCraft.getPlugin(EduCraft.class).getDataFolder().getPath(), "levels",
                config.getString("schematic")).toFile());

        location = new Location(
                Bukkit.getWorld(config.getString("location.world")),
                config.getInt("location.x"),
                config.getInt("location.y"),
                config.getInt("location.z"));

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
                                entity = entityManager.spawn(startLocation, Villager.class);
                                entity.addBehavior(new ResetableStationaryBehavior(startLocation, false));
                                entity.spawn();
                                block.setType(Material.AIR);
                            } else if (sign.getLine(1).equalsIgnoreCase("sheep")) {
                                ManagedEntity<Sheep> sheep = entityManager.spawn(block.getLocation().add(0.5, 0, 0.5), Sheep.class);
                                sheep.addBehavior(new ResetableStationaryBehavior(block.getLocation().add(0.5, 0, 0.5), false));
                                this.sheep.add(sheep);
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

    public void reset() {
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

        ((ResetableStationaryBehavior) entity.getBehaviors(ResetableStationaryBehavior.class).iterator().next()).reset();
        entity.spawn();
        for (ManagedEntity entity : sheep) {
            ((ResetableStationaryBehavior) entity.getBehaviors(ResetableStationaryBehavior.class).iterator().next()).reset();
            entity.spawn();
        }
    }

    /**
     * Removes the entities of this environment (the programmable entity and all sheep).
     */
    public void removeEntities() {
        entity.remove();
        for (ManagedEntity entity : sheep) {
            entity.remove();
        }
    }

    public ManagedEntity getEntity() {
        return entity;
    }

    public BlockFace getStartDirection() {
        return startDirection;
    }
}
