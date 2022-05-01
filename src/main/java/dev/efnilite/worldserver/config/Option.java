package dev.efnilite.worldserver.config;

import dev.efnilite.worldserver.WorldServer;
import dev.efnilite.worldserver.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Option {

    public static boolean AUTO_UPDATER;

    /* Tab options */
    public static boolean TAB_ENABLED;

    /* Chat options */
    public static boolean CHAT_ENABLED;
    public static boolean GLOBAL_CHAT_ENABLED;
    public static String GLOBAL_CHAT_PREFIX;
    public static String SPY_FORMAT;
    public static String GLOBAL_CHAT_FORMAT;
    public static HashMap<String, String> CHAT_FORMAT;
    public static HashMap<String, List<String>> GROUPS;

    /* Eco options */
    public static boolean ECONOMY_ENABLED;
    public static boolean ECONOMY_SWITCH_NOTIFICATION;
    public static String ECONOMY_SWITCH_FORMAT;
    public static String ECONOMY_CURRENCY_SYMBOL;
    public static List<String> ECONOMY_CURRENCY_NAMES;

    public static void init() {
        FileConfiguration config = WorldServer.getConfiguration().getFile("config");

        AUTO_UPDATER = config.getBoolean("auto-updater");

        TAB_ENABLED = config.getBoolean("tab-enabled");

        CHAT_ENABLED = config.getBoolean("chat-enabled");
        GLOBAL_CHAT_ENABLED = config.getBoolean("global-chat-enabled");
        GLOBAL_CHAT_PREFIX = config.getString("global-chat-prefix");
        GLOBAL_CHAT_FORMAT = config.getString("global-chat-format");
        SPY_FORMAT = config.getString("spy-format");
        GROUPS = new HashMap<>();
        List<String> node = Util.getNode(config, "groups");
        if (node != null) {
            for (String group : node) {
                List<String> worlds = config.getStringList("groups." + group);
                GROUPS.put(group, worlds);
            }
        }
        CHAT_FORMAT = new HashMap<>();
        node = Util.getNode(config, "chat-format");
        if (node != null) {
            for (String world : node) {
                String format = config.getString("chat-format." + world);
                CHAT_FORMAT.put(world, format);
            }
        }

        ECONOMY_ENABLED = config.getBoolean("economy-enabled");
        ECONOMY_SWITCH_NOTIFICATION = config.getBoolean("economy-switch-notification");
        ECONOMY_SWITCH_FORMAT = config.getString("economy-switch-format");
        ECONOMY_CURRENCY_SYMBOL = config.getString("economy-currency-symbol");
        ECONOMY_CURRENCY_NAMES = config.getStringList("economy-currency-names");
    }

    // Gets the worlds from a group name
    public static List<World> getWorlds(String group) {
        List<World> worlds = new ArrayList<>();
        for (String name : GROUPS.get(group)) {
            World world = Bukkit.getWorld(name);
            if (world == null) {
                continue;
            }
            worlds.add(world);
        }
        return worlds;
    }

    /**
     * Gets a group from a world name.
     * todo caching
     *
     * @param   world
     *          The world
     *
     * @return the World Group, or the world name if it is not in a group
     */
    public static String getGroupFromWorld(World world) {
        String name = world.getName();
        for (String group : GROUPS.keySet()) {
            for (String loopWorld : GROUPS.get(group)) {
                if (name.equals(loopWorld)) {
                    return group;
                }
            }
        }
        return "";
    }
}