package de.craften.plugins.educraft;

import de.craften.plugins.educraft.environment.EduCraftEnvironment;
import de.craften.plugins.educraft.inventory.BotInventory;
import de.craften.plugins.educraft.luaapi.EduCraftApi;
import de.craften.plugins.educraft.util.MessageSender;
import org.apache.commons.lang.StringUtils;
import org.bukkit.*;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

/**
 * An executor for EduCraft Lua scripts.
 */
public class ScriptExecutor {
    public static final long DEFAULT_FUNCTION_DELAY = 1000;
    public static final long MAX_FUNCTION_DELAY = 3000;
    private final ScriptEngine engine;
    private final String code;
    private final EduCraftEnvironment environment;
    private final BotInventory inventory;
    private UUID playerId;
    private Thread thread;
    private Runnable callback;

    /**
     * Creates a new script executor for the given code.
     *
     * @param code          code to execute
     * @param environment   environment to execute the code in
     * @param player        player that runs the code
     * @param functionDelay delay between functions, in milliseconds
     */
    public ScriptExecutor(String code, EduCraftEnvironment environment, final Player player, long functionDelay) {
        this.environment = environment;
        this.code = code;
        playerId = player.getUniqueId();

        engine = new ScriptEngine();
        EduCraftApi api = new EduCraftApi(environment, Math.min(functionDelay, MAX_FUNCTION_DELAY), new MessageSender() {
            @Override
            public void sendMessage(String message) {
                ScriptExecutor.this.sendMessage(message);
            }
        });
        inventory = api.getInventory();
        engine.mergeGlobal(api);
        engine.setGlobal("require", new RequireFunction(player));
    }

    /**
     * Runs the script in its own thread. If the script is already running,
     * it is stopped.
     */
    public void run() {
        stop();
        getEnvironment().reset(true);

        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    engine.compile(code).invoke();
                } catch (LuaError e) {
                    if (!(e.getCause() instanceof InterruptedException)) {
                        EduCraft.getPlugin(EduCraft.class).getLogger().log(Level.WARNING, "Could not execute script", e);
                        sendMessage("The program could not be executed.");
                    }

                    if (callback != null) {
                        callback.run();
                    }
                    return;
                }

                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(EduCraft.getPlugin(EduCraft.class), new Runnable() {
                    @Override
                    public void run() {
                        if (environment.fulfillsRequirements(inventory)) {
                            Location entityLocation = environment.getEntity().getEntity().getLocation();
                            Firework firework = entityLocation.getWorld().spawn(entityLocation, Firework.class);
                            FireworkMeta fwMeta = firework.getFireworkMeta();
                            fwMeta.addEffects(FireworkEffect.builder()
                                    .with(FireworkEffect.Type.STAR)
                                    .with(FireworkEffect.Type.BURST)
                                    .withColor(Color.ORANGE)
                                    .withColor(Color.SILVER)
                                    .withFlicker()
                                    .build());
                            fwMeta.setPower(2);
                            firework.setFireworkMeta(fwMeta);
                            sendMessage("Great! You did it.");
                        } else {
                            sendMessage("Oh no, that didn't work yet. Try again!");
                        }

                        if (callback != null) {
                            callback.run();
                        }
                    }
                });
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Stops the script if it is running. This does not reset the environment.
     */
    public void stop() {
        if (thread != null) {
            thread.interrupt();
            thread = null;
        }
    }

    public void setCallback(Runnable callback) {
        this.callback = callback;
    }

    public void sendMessage(String message) {
        Player player = Bukkit.getPlayer(playerId);
        if (player != null) {
            player.sendMessage("[EduCraft] " + message);
        }
    }

    /**
     * Gets the ID of the player that started this executor.
     *
     * @return the ID of the player that started this executor
     */
    public UUID getPlayerId() {
        return playerId;
    }

    /**
     * Gets the environment this executor runs the code in.
     *
     * @return the environment this executor runs the code in
     */
    public EduCraftEnvironment getEnvironment() {
        return environment;
    }

    private class RequireFunction extends VarArgFunction {
        private final Map<String, Varargs> modules = new HashMap<>();
        private final Player player;

        public RequireFunction(Player player) {
            this.player = player;
        }

        @Override
        public Varargs invoke(Varargs args) {
            Varargs module = modules.get(args.checkjstring(1).toLowerCase());
            if (module != null) {
                return module;
            }

            module = tryFindModule(args.checkjstring(1), player.getInventory());
            if (module != null) {
                return module;
            }

            module = tryFindModule(args.checkjstring(1), player.getEnderChest());
            if (module != null) {
                return module;
            }

            return LuaValue.NIL;
        }

        private Varargs tryFindModule(String name, Inventory inventory) {
            for (ItemStack item : inventory) {
                if (item != null && (item.getType() == Material.BOOK_AND_QUILL || item.getType() == Material.WRITTEN_BOOK)) {
                    BookMeta book = (BookMeta) item.getItemMeta();
                    if (book.getTitle() != null && book.getTitle().equalsIgnoreCase(name)) {
                        String code = ChatColor.stripColor(StringUtils.join(book.getPages(), "\n"));
                        Varargs module = engine.compile(code).invoke();
                        modules.put(name.toLowerCase(), module);
                        return module;
                    }
                }
            }
            return null;
        }
    }
}
