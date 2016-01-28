package de.craften.plugins.educraft;

import de.craften.plugins.educraft.environment.EduCraftEnvironment;
import de.craften.plugins.educraft.inventory.BotInventory;
import de.craften.plugins.educraft.luaapi.EduCraftApi;
import de.craften.plugins.educraft.luaapi.math.MathLib;
import de.craften.plugins.educraft.util.LuaInvoker;
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

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * An executor for EduCraft Lua scripts.
 */
public class ScriptExecutor {
    public static final long DEFAULT_FUNCTION_DELAY = 1000;
    public static final long MAX_FUNCTION_DELAY = 3000;
    private String moduleRepositoryUrl;
    private final ScriptEngine engine;
    private final String code;
    private final EduCraftEnvironment environment;
    private final BotInventory inventory;
    private final AtomicBoolean errorOccurred = new AtomicBoolean(false);
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
                ScriptExecutor.this.sendMessages(message);
            }
        }, new LuaInvoker() {
            @Override
            public Varargs safeInvoke(LuaValue function, Varargs args) {
                return ScriptExecutor.this.safeInvoke(function, args);
            }
        });
        inventory = api.getInventory();
        engine.mergeGlobal(api);
        engine.setGlobal("require", new RequireFunction(player));
        engine.setGlobal("math", new MathLib());
    }

    /**
     * Gets the URL of the module repository.
     *
     * @return URL of the module repository or null if no repository is configured
     */
    public String getModuleRepositoryUrl() {
        return moduleRepositoryUrl;
    }

    /**
     * Sets the URL of the module repository.
     *
     * @param moduleRepositoryUrl URL of the module repository or null to disable remote modules
     */
    public void setModuleRepositoryUrl(String moduleRepositoryUrl) {
        this.moduleRepositoryUrl = moduleRepositoryUrl;
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
                LuaValue chunk;
                try {
                    chunk = engine.compile(code, "<main>");
                } catch (LuaError e) {
                    sendMessages("The program could not be compiled.", e.getMessage());
                    if (callback != null) {
                        callback.run();
                    }
                    return;
                }

                try {
                    ScriptExecutor.this.safeInvoke(chunk, LuaValue.NONE);
                } catch (LuaError e) {
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
                            sendMessages("Great! You did it.");
                        } else {
                            sendMessages("Oh no, that didn't work yet. Try again!");
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

    public void sendMessages(String... messages) {
        Player player = Bukkit.getPlayer(playerId);
        if (player != null) {
            for (String message : messages) {
                player.sendMessage("[EduCraft] " + message);
            }
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

    private Varargs safeInvoke(LuaValue function, Varargs args) {
        try {
            return function.invoke(args);
        } catch (final LuaError e) {
            //only send one error message for program (the "stack trace" for java callbacks is useless anyway)
            if (!errorOccurred.getAndSet(true)) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(EduCraft.getPlugin(EduCraft.class), new Runnable() {
                    @Override
                    public void run() {
                        sendMessages("An error occurred while running your program.", e.getMessage());
                    }
                });
            }
            throw e;
        }
    }

    private class RequireFunction extends VarArgFunction {
        private final Map<String, Varargs> modules = new HashMap<>();
        private final Player player;

        public RequireFunction(Player player) {
            this.player = player;
        }

        @Override
        public Varargs invoke(Varargs args) {
            String moduleName = args.checkjstring(1);
            Varargs module = modules.get(moduleName.toLowerCase());
            if (module != null) {
                return module;
            }

            module = tryFindModule(moduleName, player.getInventory());
            if (module != null) {
                return module;
            }

            module = tryFindModule(moduleName, player.getEnderChest());
            if (module != null) {
                return module;
            }

            if (isRemoteModule(moduleName)) {
                module = tryFindRemoteModule(moduleName);
                if (module != null) {
                    return module;
                }
            }

            return LuaValue.NIL;
        }

        private boolean isRemoteModule(String moduleName) {
            return moduleName.matches("@([a-zA-Z0-9\\-_]+)/([a-zA-Z0-9\\-_]+)");
        }

        private Varargs tryFindModule(String name, Inventory inventory) {
            for (ItemStack item : inventory) {
                if (item != null && (item.getType() == Material.BOOK_AND_QUILL || item.getType() == Material.WRITTEN_BOOK)) {
                    BookMeta book = (BookMeta) item.getItemMeta();
                    if (book.getTitle() != null && book.getTitle().equalsIgnoreCase(name)) {
                        String code = ChatColor.stripColor(StringUtils.join(book.getPages(), "\n"));
                        Varargs module = engine.compile(code, name).invoke();
                        modules.put(name.toLowerCase(), module);
                        return module;
                    }
                }
            }
            return null;
        }

        private Varargs tryFindRemoteModule(String name) {
            if (moduleRepositoryUrl == null) {
                return null;
            }

            try {
                URL url = new URL(moduleRepositoryUrl + "/" + name.substring(1));
                URLConnection connection = url.openConnection();
                int responseCode = ((HttpURLConnection) connection).getResponseCode();
                switch (responseCode) {
                    case 200:
                        try (InputStream inputStream = connection.getInputStream()) {
                            Varargs module = engine.compile(inputStream, name).invoke();
                            modules.put(name.toLowerCase(), module);
                            return module;
                        } catch (IOException e) {
                            throw new LuaError(e);
                        }
                    case 404:
                        return null;
                    default:
                        throw new LuaError("Could not get module " + name + " (Error " + responseCode + ")");
                }
            } catch (IOException e) {
                throw new LuaError(e);
            }
        }
    }
}
