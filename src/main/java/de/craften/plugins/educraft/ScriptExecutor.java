package de.craften.plugins.educraft;

import org.luaj.vm2.LuaClosure;
import org.luaj.vm2.LuaValue;

/**
 * An executor for EduCraft Lua scripts.
 */
public class ScriptExecutor {
    private final ScriptEngine engine;
    private final LuaValue chunk;
    private Thread thread;
    private LuaClosure closure;

    /**
     * Creates a new script executor for the given code.
     *
     * @param code code to execute
     */
    public ScriptExecutor(String code) {
        engine = new ScriptEngine();
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
                chunk.invoke();
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
}
