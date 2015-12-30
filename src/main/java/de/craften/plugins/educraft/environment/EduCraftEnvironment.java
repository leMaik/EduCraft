package de.craften.plugins.educraft.environment;

import de.craften.plugins.educraft.EduCraft;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.Vector;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * An in-game environment that programs can be run in.
 */
public class EduCraftEnvironment {
    private final Schematic schematic;
    private final Location location;
    private final Vector startLocation;

    public EduCraftEnvironment(ConfigurationSection config) throws IOException {
        schematic = new Schematic(Paths.get(
                EduCraft.getPlugin(EduCraft.class).getDataFolder().getPath(), "levels",
                config.getString("schematic")).toFile());

        location = new Location(
                Bukkit.getWorld(config.getString("location.world")),
                config.getInt("location.x"),
                config.getInt("location.y"),
                config.getInt("location.z"));

        startLocation = new Vector(
                config.getInt("startLocation.x"),
                config.getInt("startLocation.y"),
                config.getInt("startLocation.z"));
    }

    public void reset() {
        schematic.restoreAt(location);

        //TODO kill and respawn entities
    }
}
