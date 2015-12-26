package de.craften.plugins.educraft;

import de.craften.plugins.managedentities.EntityManager;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main class of the EduCraft plugin.
 */
public class EduCraft extends JavaPlugin {
    private EntityManager manager;

    @Override
    public void onEnable() {
        manager = new EntityManager(this);
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
            }
        }

        return false;
    }

    private void runCode(final Player player, String code) {
        final ScriptExecutor executor = new ScriptExecutor(code, manager.spawn(player.getLocation(), Creeper.class), player);
        executor.setCallback(new Runnable() {
            @Override
            public void run() {
                executor.sendMessage("Done.");
            }
        });
        executor.run();
    }
}
