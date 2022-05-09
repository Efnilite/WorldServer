package dev.efnilite.worldserver.config;

import dev.efnilite.worldserver.WorldServer;
import dev.efnilite.worldserver.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Option {

    public static boolean AUTO_UPDATER;

    public static HashMap<String, List<String>> GROUPS;

    /* Tab options */
    public static boolean TAB_ENABLED;

    /* Chat options */
    public static boolean CHAT_ENABLED;
    public static boolean GLOBAL_CHAT_ENABLED;
    public static String GLOBAL_CHAT_PREFIX;
    public static String SPY_FORMAT;
    public static String GLOBAL_CHAT_FORMAT;
    public static Map<String, String> CHAT_FORMAT;
    public static Map<String, Double> CHAT_COOLDOWN;
    public static String CHAT_COOLDOWN_FORMAT;
    public static List<String> CHAT_BLOCKED;
    public static String CHAT_BLOCKED_FORMAT;
    public static boolean CHAT_AFFIXES;

    /* Eco options */
    public static boolean ECONOMY_ENABLED;
    public static boolean ECONOMY_GLOBAL_ENABLED;
    public static boolean ECONOMY_SWITCH_NOTIFICATION;
    public static String ECONOMY_SWITCH_FORMAT;
    public static String ECONOMY_CURRENCY_SYMBOL;
    public static List<String> ECONOMY_CURRENCY_NAMES;
    public static Map<String, Double> ECONOMY_STARTING_AMOUNT;
    public static boolean ECONOMY_OVERRIDE_BALANCE_COMMAND;
    public static String ECONOMY_BALANCE_FORMAT;
    public static boolean ECONOMY_OVERRIDE_PAY_COMMAND;
    public static String ECONOMY_PAY_NO_FUNDS_FORMAT;
    public static String ECONOMY_PAY_SEND_FORMAT;
    public static String ECONOMY_PAY_RECEIVE_FORMAT;
    public static boolean ECONOMY_OVERRIDE_BALTOP_COMMAND;

    public static void init() {
        FileConfiguration config = WorldServer.getConfiguration().getFile("config");

        AUTO_UPDATER = config.getBoolean("auto-updater");

        GROUPS = new HashMap<>();
        List<String> node = Util.getNode(config, "groups");
        if (node != null) {
            for (String group : node) {
                List<String> worlds = config.getStringList("groups." + group);
                GROUPS.put(group, worlds);
            }
        }

        TAB_ENABLED = config.getBoolean("tab-enabled");

        CHAT_ENABLED = config.getBoolean("chat-enabled");
        GLOBAL_CHAT_ENABLED = config.getBoolean("global-chat-enabled");
        GLOBAL_CHAT_PREFIX = config.getString("global-chat-prefix");
        GLOBAL_CHAT_FORMAT = config.getString("global-chat-format");
        SPY_FORMAT = config.getString("spy-format");

        CHAT_FORMAT = new HashMap<>();
        node = Util.getNode(config, "chat-format");
        if (node != null) {
            for (String world : node) {
                String format = config.getString("chat-format." + world);
                CHAT_FORMAT.put(world, format);
            }
        }

        CHAT_COOLDOWN = new HashMap<>();
        node = Util.getNode(config, "chat-cooldown");
        if (node != null) {
            for (String world : node) {
                double format = config.getDouble("chat-cooldown." + world);
                CHAT_COOLDOWN.put(world, format);
            }
        }
        CHAT_COOLDOWN_FORMAT = config.getString("chat-cooldown-format");
        CHAT_BLOCKED = config.getStringList("chat-blocked");
        CHAT_BLOCKED_FORMAT = config.getString("chat-blocked-format");
        CHAT_AFFIXES = config.getBoolean("chat-affixes");

        ECONOMY_ENABLED = config.getBoolean("economy-enabled");
        ECONOMY_GLOBAL_ENABLED = config.getBoolean("economy-global-enabled");
        ECONOMY_SWITCH_NOTIFICATION = config.getBoolean("economy-switch-notification");
        ECONOMY_SWITCH_FORMAT = config.getString("economy-switch-format");
        ECONOMY_CURRENCY_SYMBOL = config.getString("economy-currency-symbol");
        ECONOMY_CURRENCY_NAMES = config.getStringList("economy-currency-names");
        ECONOMY_STARTING_AMOUNT = new HashMap<>();
        node = Util.getNode(config, "economy-starting-amount");
        if (node != null) {
            for (String world : node) {
                double amount = config.getDouble("economy-starting-amount." + world);
                ECONOMY_STARTING_AMOUNT.put(world, amount);
            }
        }
        ECONOMY_OVERRIDE_BALANCE_COMMAND = config.getBoolean("economy-override-balance-command");
        ECONOMY_BALANCE_FORMAT = config.getString("economy-balance-format");
        ECONOMY_OVERRIDE_PAY_COMMAND = config.getBoolean("economy-override-pay-command");
        ECONOMY_PAY_NO_FUNDS_FORMAT = config.getString("economy-pay-no-funds-format");
        ECONOMY_PAY_SEND_FORMAT = config.getString("economy-pay-send-format");
        ECONOMY_PAY_RECEIVE_FORMAT = config.getString("economy-pay-receive-format");
        ECONOMY_OVERRIDE_BALTOP_COMMAND = config.getBoolean("economy-override-baltop-command");
    }

    /**
     * Gets the worlds from a group name. If there is no associated group, it returns a single-item list with the World instead.
     *
     * @param   group
     *          The group/world name
     *
     * @return a list with worlds belonging to the specified group/world.
     */
    public static List<World> getWorlds(String group) {
        List<World> worlds = new ArrayList<>();
        List<String> worldNames = GROUPS.get(group);

        if (worldNames != null) {
            for (String name : worldNames) {
                World world = Bukkit.getWorld(name);
                if (world == null) {
                    continue;
                }
                worlds.add(world);
            }
        } else {
            worlds.add(Bukkit.getWorld(group));
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
            List<String> names = GROUPS.get(group);

            if (names == null) {
                return name;
            }

            for (String loopWorld : names) {
                if (name.equals(loopWorld)) {
                    return group;
                }
            }
        }
        return name;
    }
}