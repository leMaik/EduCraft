package de.craften.plugins.educraft;

import de.craften.plugins.educraft.environment.EduCraftEnvironment;
import de.craften.plugins.educraft.luaapi.EduCraftApi;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

import java.util.UUID;
import java.util.logging.Level;

/**
 * An executor for EduCraft Lua scripts.
 */
public class ScriptExecutor {
    public static final long FUNCTION_DELAY = 1000;
    private final ScriptEngine engine;
    private final LuaValue chunk;
    private final EduCraftEnvironment environment;
    private UUID playerId;
    private Thread thread;
    private Runnable callback;

    /**
     * Creates a new script executor for the given code.
     *
     * @param code        code to execute
     * @param environment environment to execute the code in
     * @param player      player that runs the code
     */
    public ScriptExecutor(String code, EduCraftEnvironment environment, Player player) {
        this.environment = environment;

        engine = new ScriptEngine();
        engine.mergeGlobal(new EduCraftApi(environment.getEntity(), environment.getStartDirection()));
        engine.setGlobal("log", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue message) {
                sendMessage(message.tojstring());
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

        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(FUNCTION_DELAY);
                } catch (InterruptedException e) {
                    EduCraft.getPlugin(EduCraft.class).getLogger().log(Level.WARNING, "Could not execute script", e);
                    sendMessage("The program could not be executed.");
                    return;
                }

                try {
                    chunk.invoke();
                } catch (LuaError e) {
                    if (!(e.getCause() instanceof InterruptedException)) {
                        EduCraft.getPlugin(EduCraft.class).getLogger().log(Level.WARNING, "Could not execute script", e);
                        sendMessage("The program could not be executed.");
                    }
                    return;
                }

                if (callback != null) {
                    callback.run();
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Stops the script if it is running, and resets the environment.
     */
    public void stop() {
        if (thread != null) {
            thread.interrupt();
            thread = null;
        }

        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(EduCraft.getPlugin(EduCraft.class), new Runnable() {
            @Override
            public void run() {
                environment.reset();
            }
        });
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
}
