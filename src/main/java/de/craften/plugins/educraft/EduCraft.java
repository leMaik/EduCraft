package de.craften.plugins.educraft;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import de.craften.plugins.educraft.environment.EduCraftEnvironment;
import de.craften.plugins.educraft.environment.EnvironmentProtection;
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.Collection;
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
                stopAllPrograms(event.getPlayer().getUniqueId());
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

        getServer().getPluginManager().registerEvents(new EnvironmentProtection(), this);
    }

    @Override
    public void onDisable() {
        for (ScriptExecutor executor : runningPrograms.values()) {
            executor.stop();
        }

        for (EduCraftEnvironment environment : levels.values()) {
            environment.reset(false);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length >= 1) {
                if (args[0].equals("run") && sender.hasPermission("educraft.run")
                        && args.length >= 2) {
                    ItemStack item = player.getItemInHand();
                    if (item.getType() == Material.BOOK_AND_QUILL || item.getType() == Material.WRITTEN_BOOK) {
                        BookMeta book = (BookMeta) player.getItemInHand().getItemMeta();
                        EduCraftEnvironment environment = levels.get(args[1]);
                        long delay = args.length == 3 ? Long.parseLong(args[2]) : ScriptExecutor.DEFAULT_FUNCTION_DELAY;
                        if (environment != null) {
                            if (!environment.isLocked()) {
                                player.sendMessage("[EduCraft] Running your code...");
                                runCode(player, ChatColor.stripColor(StringUtils.join(book.getPages(), "\n")), levels.get(args[1]), delay);
                            } else {
                                player.sendMessage("[EduCraft] Someone else is running code in that environment, try another one.");
                            }
                        } else {
                            player.sendMessage("[EduCraft] There is no environment with that name.");
                        }
                    } else {
                        player.sendMessage("[EduCraft] Use this command while holding a book with code.");
                    }
                    return true;
                } else if (args[0].equals("reset") && args.length == 2 && sender.hasPermission("educraft.reset")) {
                    EduCraftEnvironment environment = levels.get(args[1]);
                    if (environment != null) {
                        ScriptExecutor executor = getExecutorIn(environment);
                        if (executor != null) {
                            if (sender.hasPermission("educraft.stop.any") || (sender instanceof Player && executor.getPlayerId().equals(((Player) sender).getUniqueId()))) {
                                stopProgram(executor);
                            } else {
                                player.sendMessage("[EduCraft] Someone else is running code in that environment, you can't reset it now.");
                                return true;
                            }
                        }
                        environment.reset(true);
                        player.sendMessage("[EduCraft] Environment " + args[1] + " reset.");
                    } else {
                        player.sendMessage("[EduCraft] There is no environment with that name.");
                    }
                    return true;
                } else if (args[0].equals("stop") && sender.hasPermission("educraft.stop")) {
                    if (args.length == 2) {
                        EduCraftEnvironment environment = levels.get(args[1]);
                        if (environment != null) {
                            ScriptExecutor executor = getExecutorIn(environment);
                            if (executor != null) {
                                if ((sender instanceof Player && executor.getPlayerId().equals(((Player) sender).getUniqueId()))) {
                                    stopProgram(executor);
                                } else if (sender.hasPermission("educraft.stop.any")) {
                                    stopProgram(executor);
                                    sender.sendMessage("[EduCraft] Program stopped.");
                                } else {
                                    sender.sendMessage("[EduCraft] You have no permission to stop the program that runs in that environment.");
                                }
                            } else {
                                player.sendMessage("[EduCraft] There is no program running in that environment.");
                            }
                        } else {
                            player.sendMessage("[EduCraft] There is no environment with that name.");
                        }
                    } else {
                        stopAllPrograms(player.getUniqueId());
                    }
                    return true;
                }
            }
        }

        return false;
    }

    private ScriptExecutor getExecutorIn(EduCraftEnvironment environment) {
        for (ScriptExecutor currentExecutor : runningPrograms.values()) {
            if (currentExecutor.getEnvironment() == environment) {
                return currentExecutor;
            }
        }
        return null;
    }

    private void stopAllPrograms(UUID playerId) {
        for (ScriptExecutor executor : runningPrograms.removeAll(playerId)) {
            stopProgram(executor);
        }
    }

    private void stopProgram(ScriptExecutor executor) {
        executor.stop();
        executor.getEnvironment().unlock();
        runningPrograms.remove(executor.getPlayerId(), executor);
        executor.sendMessage("Stopped.");
    }

    private void runCode(Player player, String code, final EduCraftEnvironment environment, long delay) {
        final UUID playerId = player.getUniqueId();
        environment.lock(player);
        final ScriptExecutor executor = new ScriptExecutor(code, environment, player, delay);
        executor.setCallback(new Runnable() {
            @Override
            public void run() {
                runningPrograms.remove(playerId, executor);
                environment.unlock();
            }
        });
        executor.run();
        runningPrograms.put(playerId, executor);
    }

    public EntityManager getEntityManager() {
        return manager;
    }

    /**
     * Gets all environments.
     *
     * @return all environments
     */
    public Collection<EduCraftEnvironment> getEnvironments() {
        return levels.values();
    }
}
