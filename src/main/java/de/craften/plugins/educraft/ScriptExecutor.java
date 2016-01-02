package de.craften.plugins.educraft;

import de.craften.plugins.educraft.environment.EduCraftEnvironment;
import de.craften.plugins.educraft.luaapi.EduCraftApi;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
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
        engine.mergeGlobal(new EduCraftApi(environment));
        engine.setGlobal("log", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue message) {
                sendMessage("[LOG] " + message.tojstring());
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
