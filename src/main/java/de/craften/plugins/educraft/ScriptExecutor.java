package de.craften.plugins.educraft;

import de.craften.plugins.educraft.environment.EduCraftEnvironment;
import de.craften.plugins.educraft.luaapi.EduCraftApi;
import org.apache.commons.lang.StringUtils;
import org.bukkit.*;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.VarArgFunction;

import java.util.UUID;
import java.util.logging.Level;

/**
 * An executor for EduCraft Lua scripts.
 */
public class ScriptExecutor {
    public static final long DEFAULT_FUNCTION_DELAY = 1000;
    public static final long MAX_FUNCTION_DELAY = 3000;
    private final ScriptEngine engine;
    private final LuaValue chunk;
    private final EduCraftEnvironment environment;
    private final long functionDelay;
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
        this.functionDelay = Math.min(functionDelay, MAX_FUNCTION_DELAY);

        engine = new ScriptEngine();
        engine.mergeGlobal(new EduCraftApi(environment, this.functionDelay));
        engine.setGlobal("log", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue message) {
                sendMessage("[LOG] " + message.tojstring());
                return LuaValue.NIL;
            }
        });
        engine.setGlobal("require", new VarArgFunction() {
            @Override
            public Varargs invoke(Varargs args) {
                for (ItemStack item : player.getInventory()) {
                    if (item != null && (item.getType() == Material.BOOK_AND_QUILL || item.getType() == Material.WRITTEN_BOOK)) {
                        BookMeta book = (BookMeta) item.getItemMeta();
                        if (book.getTitle() != null && book.getTitle().equalsIgnoreCase(args.checkjstring(1))) {
                            String code = ChatColor.stripColor(StringUtils.join(book.getPages(), "\n"));
                            return engine.compile(code).invoke();
                        }
                    }
                }
                return LuaValue.NIL;
            }
        });
        chunk = engine.compile(code);
        playerId = player.getUniqueId();
    }

    /**
     * Runs the script in its own thread. If the script is already running,
     * it is stopped.
     */
    public void run() {
        stop();
        resetEnvironment();

        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(functionDelay);
                } catch (InterruptedException e) {
                    EduCraft.getPlugin(EduCraft.class).getLogger().log(Level.WARNING, "Could not execute script", e);
                    sendMessage("The program could not be executed.");

                    if (callback != null) {
                        callback.run();
                    }
                    return;
                }

                try {
                    chunk.invoke();
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
                        if (environment.fulfillsRequirements()) {
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
                    }
                });

                if (callback != null) {
                    callback.run();
                }
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

    public boolean isRunning() {
        return thread != null;
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
     * Resets the environment this script is executed in.
     */
    public void resetEnvironment() {
        environment.reset(true);
    }
}
