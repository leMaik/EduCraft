package de.craften.plugins.educraft;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import de.craften.plugins.educraft.environment.EduCraftEnvironment;
import de.craften.plugins.managedentities.EntityManager;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Main class of the EduCraft plugin.
 */
public class EduCraft extends JavaPlugin {
    private final Multimap<UUID, ScriptExecutor> runningPrograms = ArrayListMultimap.create();
    private final Map<String, EduCraftEnvironment> levels = new HashMap<>();
    private EntityManager manager;

    @Override
    public void onEnable() {
        manager = new EntityManager(this);

        getServer().getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onPlayerQuit(PlayerQuitEvent event) {
                killAll(event.getPlayer().getUniqueId());
            }
        }, this);

        int loadedLevels = 0;
        ConfigurationSection levelsSection = getConfig().getConfigurationSection("levels");
        if (levelsSection != null) {
            for (String key : levelsSection.getKeys(false)) {
                try {
                    levels.put(key, new EduCraftEnvironment(levelsSection.getConfigurationSection(key)));
                    loadedLevels++;
                } catch (IOException e) {
                    getLogger().log(Level.WARNING, "Could not load level " + key, e);
                }
            }
        }
        getLogger().info(loadedLevels + " levels loaded");
    }

    @Override
    public void onDisable() {
        for (EduCraftEnvironment environment : levels.values()) {
            environment.removeEntities();
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (label.equals("run") && player.getItemInHand().getType() == Material.BOOK_AND_QUILL) {
                BookMeta book = (BookMeta) player.getItemInHand().getItemMeta();
                player.sendMessage("[EduCraft] Running your code...");
                runCode(player, ChatColor.stripColor(StringUtils.join(book.getPages(), " ")), levels.get(args[0]));
                return true;
            } else if (label.equals("stop")) {
                killAll(player.getUniqueId());
            }
        }

        return false;
    }

    private void killAll(UUID playerId) {
        for (ScriptExecutor executor : runningPrograms.removeAll(playerId)) {
            executor.stop();
            executor.sendMessage("Stopped.");
        }
    }

    private void runCode(Player player, String code, EduCraftEnvironment environment) {
        final UUID playerId = player.getUniqueId();
        final ScriptExecutor executor = new ScriptExecutor(code, environment, player);
        executor.setCallback(new Runnable() {
            @Override
            public void run() {
                runningPrograms.remove(playerId, executor);
                executor.sendMessage("Done.");
            }
        });
        executor.run();
        runningPrograms.put(playerId, executor);
    }

    public EntityManager getEntityManager() {
        return manager;
    }
}
