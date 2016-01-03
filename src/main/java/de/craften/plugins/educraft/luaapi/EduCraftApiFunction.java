package de.craften.plugins.educraft.luaapi;

import de.craften.plugins.educraft.EduCraft;
import org.bukkit.Bukkit;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

/**
 * A function for the EduCraft Lua API.
 */
public abstract class EduCraftApiFunction extends VarArgFunction {
    private EduCraftApi api;

    /**
     * Gets the API.
     *
     * @return the API
     */
    public EduCraftApi getApi() {
        return api;
    }

    /**
     * Sets the API.
     *
     * @param api the API
     * @return this instance
     */
    protected EduCraftApiFunction withApi(EduCraftApi api) {
        this.api = api;
        return this;
    }

    @Override
    public Varargs invoke(final Varargs varargs) {
        try {
            Varargs returnValue = Bukkit.getScheduler().callSyncMethod(EduCraft.getPlugin(EduCraft.class), new Callable<Varargs>() {
                @Override
                public Varargs call() throws Exception {
                    return execute(varargs);
                }
            }).get();
            Thread.sleep(Math.max(25, getApi().getFunctionDelay()));
            Bukkit.getScheduler().callSyncMethod(EduCraft.getPlugin(EduCraft.class), new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    afterExecute();
                    return null;
                }
            }).get();
            return returnValue;
        } catch (InterruptedException | ExecutionException e) {
            throw new LuaError(e);
        }
    }

    public abstract Varargs execute(Varargs varargs);

    /**
     * Method that is called after {@link #execute(Varargs)} was called and the delay is over.
     */
    protected void afterExecute() {
    }
}
