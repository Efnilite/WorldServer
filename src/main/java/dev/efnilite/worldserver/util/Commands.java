package dev.efnilite.worldserver.util;

import dev.efnilite.vilib.ViMain;
import dev.efnilite.vilib.util.Version;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.SimpleCommandMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Reflections required to update the internal CommandMap
 */
public class Commands {

    /**
     * Retrieves the current command map instance
     *
     * @return the command map instance
     */
    public static @Nullable SimpleCommandMap retrieveMap() {
        try {
            Field field = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            field.setAccessible(true);
            return (SimpleCommandMap) field.get(Bukkit.getServer());
        } catch (NoSuchFieldException | IllegalAccessException ex) {
            ex.printStackTrace();
            ViMain.logging().error("Error while trying to access the command map.");
            ViMain.logging().error("Commands will not show up on completion.");
            return null;
        }
    }

    /**
     * Adds a command to the Command Map
     *
     * @param   alias
     *          The alias
     *
     * @param   command
     *          The command instance
     *
     * @return the command that was added
     */
    public static Command add(@NotNull String alias, @NotNull Command command) {
        try {
            Field field = SimpleCommandMap.class.getDeclaredField("knownCommands");
            field.setAccessible(true);

            CommandMap map = retrieveMap();

            Map<String, Command> knownCommands = (Map<String, Command>) field.get(map);

            Command prev1 = knownCommands.put("ws:" + alias, command);
            Command prev2 = knownCommands.put(alias, command);

            field.set(map, knownCommands);

            return prev1 == null ? prev2 : prev1;
        } catch (NoSuchFieldException ex) {
            ViMain.logging().stack("knownCommands field not found for registry",
                    "update your server or switch to a supported server platform", ex);
            return null;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            ViMain.logging().error("There was an error while trying to register your command to the Command Map");
            ViMain.logging().error("It might not show up in-game in the auto-complete, but it does work.");
            return null;
        }
    }
}