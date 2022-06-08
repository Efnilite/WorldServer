package dev.efnilite.worldserver.util;

import dev.efnilite.vilib.chat.Message;
import dev.efnilite.vilib.command.ViCommand;
import dev.efnilite.worldserver.WorldServer;
import dev.efnilite.worldserver.hook.PlaceholderHook;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Util {

    public static final NumberFormat CURRENCY_FORMAT = NumberFormat.getInstance(Locale.US);

    static {
        CURRENCY_FORMAT.setRoundingMode(RoundingMode.FLOOR);
        CURRENCY_FORMAT.setGroupingUsed(true);
        CURRENCY_FORMAT.setMinimumFractionDigits(2);
        CURRENCY_FORMAT.setMaximumFractionDigits(2);
    }

    public static void send(Player player, String message) {
        Message.send(player, PlaceholderHook.translate(player, message));
    }

    /**
     * Gets the size of a ConfigurationSection
     *
     * @param   file
     *          The file
     *
     * @param   path
     *          The path
     */
    public static @Nullable List<String> getNode(FileConfiguration file, String path) {
        ConfigurationSection section = file.getConfigurationSection(path);
        if (section == null) {
            return null;
        }
        return new ArrayList<>(section.getKeys(false));
    }

    /**
     * Registers a command to the CommandMap, making it show up as a command
     *
     * @param   alias
     *          The name of the command
     *
     * @param   command
     *          The details associated with this aliased command.
     */
    public static Command registerToMap(String alias, ViCommand command) {
        BukkitCommand pluginCommand = new BukkitCommand(alias, command);

        return addToKnown(alias, pluginCommand, retrieveMap());
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
     * @param   alias
     *          The alias
     *
     * @param   command
     *          The command instance
     *
     * @param   map
     *          The commandmap instance to write this to
     *
     * @return the command that was added
     */
    public static Command addToKnown(String alias, Command command, CommandMap map) {
        try {
            Field field = SimpleCommandMap.class.getDeclaredField("knownCommands");
            field.setAccessible(true);

            Map<String, Command> knownCommands = (Map<String, Command>) field.get(map);

            Command prev1 = knownCommands.put("ws:" + alias, command);
            Command prev2 = knownCommands.put(alias, command);

            field.set(map, knownCommands);

            return prev1 == null ? prev2 : prev1;
        } catch (NoSuchFieldException ex) {
            WorldServer.logging().stack("knownCommands field not found for registry",
                    "update your server or switch to a supported server platform", ex);
            return null;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            WorldServer.logging().error("There was an error while trying to register your command to the Command Map");
            WorldServer.logging().error("It might not show up in-game in the auto-complete, but it does work.");
            return null;
        }
    }

    public static boolean isLatest(String latest, String current) {
        int latestVs = Integer.parseInt(stripLatest(latest));
        int currentVs = Integer.parseInt(stripLatest(current));

        return latestVs <= currentVs;
    }

    private static String stripLatest(String string) {
        return string.toLowerCase().replace("v", "").replace(".", "");
    }
}