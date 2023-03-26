package dev.efnilite.worldserver.util;

import dev.efnilite.vilib.command.ViCommand;
import dev.efnilite.vilib.util.Strings;
import dev.efnilite.worldserver.WorldServer;
import dev.efnilite.worldserver.hook.PlaceholderHook;
import net.md_5.bungee.api.ChatColor;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {

    public static final NumberFormat CURRENCY_FORMAT = NumberFormat.getInstance(Locale.US);

    static {
        CURRENCY_FORMAT.setRoundingMode(RoundingMode.FLOOR);
        CURRENCY_FORMAT.setGroupingUsed(true);
        CURRENCY_FORMAT.setMinimumFractionDigits(2);
        CURRENCY_FORMAT.setMaximumFractionDigits(2);
    }

    public static void send(CommandSender sender, String message) {
        sender.sendMessage(Strings.colour(sender instanceof Player ? PlaceholderHook.translate((Player) sender, message) : message));
    }

    /**
     * Gets the size of a ConfigurationSection
     *
     * @param file The file
     * @param path The path
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

    public static boolean isLatest(String latest, String current) {
        return toVersion(latest) <= toVersion(current);
    }

    private static double toVersion(String version) {
        String stripped = version.toLowerCase().replace("v", "").replace(".", "");

        return Double.parseDouble(stripped) / stripped.length();
    }

    private static final Pattern HEX_PATTERN = Pattern.compile("&(#\\w{6})");

    public static String colorLegacy(String message) {
        Matcher matcher = HEX_PATTERN.matcher(ChatColor.translateAlternateColorCodes('&', message));
        StringBuffer buffer = new StringBuffer();

        while (matcher.find()) {
            matcher.appendReplacement(buffer, ChatColor.of(matcher.group(1)).toString());
        }

        return matcher.appendTail(buffer).toString();
    }
}