package de.craften.plugins.educraft;

import de.craften.plugins.educraft.luaapi.EduCraftApi;
import de.craften.plugins.managedentities.ManagedEntity;
import org.luaj.vm2.LuaClosure;
import org.luaj.vm2.LuaValue;

import java.util.logging.Level;

/**
 * An executor for EduCraft Lua scripts.
 */
public class ScriptExecutor {
    public static final long MOVEMENT_DELAY = 1000;
    private final ScriptEngine engine;
    private final LuaValue chunk;
    private Thread thread;
    private LuaClosure closure;
    private Runnable callback;

    /**
     * Creates a new script executor for the given code.
     *
     * @param code   code to execute
     * @param entity entity to execute the code with
     */
    public ScriptExecutor(String code, ManagedEntity entity) {
        engine = new ScriptEngine();
        engine.installGlobal(new EduCraftApi(entity));
        chunk = engine.compile(code);
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
                    Thread.sleep(MOVEMENT_DELAY);
                } catch (InterruptedException e) {
                    EduCraft.getPlugin(EduCraft.class).getLogger().log(Level.WARNING, "Could not execute script", e);
                }

                chunk.invoke();
                if (callback != null) {
                    callback.run();
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Stops the script if it is running.
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
}
