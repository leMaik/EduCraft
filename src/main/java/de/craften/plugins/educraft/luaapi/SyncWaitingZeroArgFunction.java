package de.craften.plugins.educraft.luaapi;

import de.craften.plugins.educraft.EduCraft;
import de.craften.plugins.educraft.ScriptExecutor;
import org.bukkit.Bukkit;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ZeroArgFunction;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

/**
 * A function that runs the code synchronously and then sleeps
 * for {@link de.craften.plugins.educraft.ScriptExecutor#MOVEMENT_DELAY}.
 */
public abstract class SyncWaitingZeroArgFunction extends ZeroArgFunction {
    @Override
    public final LuaValue call() {
        try {
            Thread.sleep(ScriptExecutor.MOVEMENT_DELAY);

            return Bukkit.getScheduler().callSyncMethod(EduCraft.getPlugin(EduCraft.class), new Callable<LuaValue>() {
                @Override
                public LuaValue call() throws Exception {
                    return callSync();
                }
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            EduCraft.getPlugin(EduCraft.class).getLogger().log(Level.WARNING, "Error while executing a script", e);
            throw new LuaError("Error while calling a function");
        }
    }

    protected abstract LuaValue callSync();
}
