package de.craften.plugins.educraft;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main class of the EduCraft plugin.
 */
public class EduCraft extends JavaPlugin {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (command.getName().equals("run") && player.getItemInHand().getType() == Material.BOOK_AND_QUILL) {
                BookMeta book = (BookMeta) player.getItemInHand().getItemMeta();
                player.sendMessage("[EduCraft] Running code your...");
                runCode(player, StringUtils.join(book.getPages(), " "));
            }
        }

        return false;
    }

    private void runCode(final Player player, String code) {
        ScriptExecutor executor = new ScriptExecutor(code);
        executor.setCallback(new Runnable() {
            @Override
            public void run() {
                getServer().getScheduler().scheduleSyncDelayedTask(EduCraft.this, new Runnable() {
                    @Override
                    public void run() {
                        player.sendMessage("Done.");
                    }
                });
            }
        });
        executor.run();
    }
}
