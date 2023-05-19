package dev.efnilite.worldserver.util;

import dev.efnilite.vilib.command.ViCommand;
import dev.efnilite.worldserver.WorldServer;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.Map;

public class Commands {


    /**
     * Registers a command to the CommandMap, making it show up as a command
     *
     * @param alias   The name of the command
     * @param command The details associated with this aliased command.
     */
    public static void registerToMap(String alias, ViCommand command) {
        BukkitCommand pluginCommand = new BukkitCommand(alias, command);

        addToKnown(alias, pluginCommand, retrieveMap());
    }

    /**
     * Retrieves the current command map instance.
     * Source: Efnilite/CommandFactory
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
            WorldServer.logging().error("Error while trying to access the command map.");
            WorldServer.logging().error("Commands will not show up on completion.");
            return null;
        }
    }

    /**
     * Adds a command to the Command Map
     * Source: Efnilite/CommandFactory
     *
     * @param alias   The alias
     * @param command The command instance
     * @param map     The commandmap instance to write this to
     */
    public static void addToKnown(String alias, Command command, CommandMap map) {
        try {
            Field field = SimpleCommandMap.class.getDeclaredField("knownCommands");
            field.setAccessible(true);

            Map<String, Command> knownCommands = (Map<String, Command>) field.get(map);

            knownCommands.put("ws:" + alias, command);
            knownCommands.put(alias, command);

            field.set(map, knownCommands);
        } catch (NoSuchFieldException ex) {
            WorldServer.logging().stack("knownCommands field not found for registry", "update your server or switch to a supported server platform", ex);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            WorldServer.logging().error("There was an error while trying to register your command to the Command Map");
            WorldServer.logging().error("It might not show up in-game in the auto-complete, but it does work.");
        }
    }

    private static class BukkitCommand extends Command {

        private final CommandExecutor executor;

        public BukkitCommand(String label, CommandExecutor executor) {
            super(label);
            this.executor = executor;
        }

        @Override
        public boolean execute(@NotNull CommandSender sender, @NotNull String label, String[] args) {
            executor.onCommand(sender, this, label, args);
            return true;
        }
    }
}