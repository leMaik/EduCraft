package de.craften.plugins.educraft;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import de.craften.plugins.managedentities.EntityManager;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

/**
 * Main class of the EduCraft plugin.
 */
public class EduCraft extends JavaPlugin {
    private EntityManager manager;
    private Multimap<UUID, ScriptExecutor> runningPrograms = ArrayListMultimap.create();

    @Override
    public void onEnable() {
        manager = new EntityManager(this);

        getServer().getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onPlayerQuit(PlayerQuitEvent event) {
                killAll(event.getPlayer().getUniqueId());
            }
        }, this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (label.equals("run") && player.getItemInHand().getType() == Material.BOOK_AND_QUILL) {
                BookMeta book = (BookMeta) player.getItemInHand().getItemMeta();
                player.sendMessage("[EduCraft] Running your code...");
                runCode(player, ChatColor.stripColor(StringUtils.join(book.getPages(), " ")));
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

    private void runCode(Player player, String code) {
        final UUID playerId = player.getUniqueId();
        final ScriptExecutor executor = new ScriptExecutor(code, manager.spawn(player.getLocation(), Villager.class), player);
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
}
