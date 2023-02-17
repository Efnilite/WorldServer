package dev.efnilite.worldserver.config;

import dev.efnilite.worldserver.WorldServer;
import dev.efnilite.worldserver.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigValue {

    public static boolean AUTO_UPDATER;

    public static HashMap<String, List<String>> GROUPS;

    public static Map<String, List<Type>> GROUPS_SHARE;

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
    public static Map<String, String> CHAT_JOIN_FORMATS;
    public static Map<String, String> CHAT_LEAVE_FORMATS;
    public static boolean CLEAR_CHAT_ON_SWITCH;

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
    public static boolean ECONOMY_BALANCE_CHANGE;
    public static String ECONOMY_BALANCE_CHANGE_FORMAT;
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

//        // add group shares
//        // if no setting is found, let group share all properties
//        GROUPS_SHARE = new HashMap<>();
//        for (String group : GROUPS.keySet()) {
//            String types = config.getString("groups-share." + group);
//
//            if (types == null) {
//                GROUPS_SHARE.put(group, Arrays.asList(Type.CHAT, Type.TAB, Type.ECO)); // add all types
//            } else {
//                String[] parts = types.replace(" ", "").split(",");
//
//                List<Type> toAdd = new ArrayList<>();
//                for (String part : parts) {
//                    toAdd.add(Type.valueOf(part.toUpperCase()));
//                }
//
//                GROUPS_SHARE.put(group, toAdd);
//            }
//        }


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

        CHAT_JOIN_FORMATS = new HashMap<>();
        node = Util.getNode(config, "chat-join-formats");
        if (node != null) {
            for (String world : node) {
                String format = config.getString("chat-join-formats." + world);
                CHAT_JOIN_FORMATS.put(world, format);
            }
        }

        CHAT_LEAVE_FORMATS = new HashMap<>();
        node = Util.getNode(config, "chat-leave-formats");
        if (node != null) {
            for (String world : node) {
                String format = config.getString("chat-leave-formats." + world);
                CHAT_LEAVE_FORMATS.put(world, format);
            }
        }

        CLEAR_CHAT_ON_SWITCH = config.getBoolean("clear-chat-on-switch");

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
        ECONOMY_BALANCE_CHANGE = config.getBoolean("economy-balance-change");
        ECONOMY_BALANCE_CHANGE_FORMAT = config.getString("economy-balance-change-format");
        ECONOMY_OVERRIDE_PAY_COMMAND = config.getBoolean("economy-override-pay-command");
        ECONOMY_PAY_NO_FUNDS_FORMAT = config.getString("economy-pay-no-funds-format");
        ECONOMY_PAY_SEND_FORMAT = config.getString("economy-pay-send-format");
        ECONOMY_PAY_RECEIVE_FORMAT = config.getString("economy-pay-receive-format");
        ECONOMY_OVERRIDE_BALTOP_COMMAND = config.getBoolean("economy-override-baltop-command");
    }


    private static final Map<String, List<World>> groupWorldsCache = new HashMap<>();

    /**
     * Gets the worlds from a group name. If there is no associated group, it returns a single-item list with the World instead.
     *
     * @param   group
     *          The group/world name
     *
     * @return a list with worlds belonging to the specified group/world.
     */
    public static List<World> getWorlds(String group) {
        if (groupWorldsCache.containsKey(group)) {
            return groupWorldsCache.get(group);
        }

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

        groupWorldsCache.put(group, worlds);

        return worlds;
    }

    private static final Map<World, String> worldGroupCache = new HashMap<>();

    /**
     * Gets a group from a world name.
     *
     * @param   world
     *          The world
     *
     * @return the World Group, or the world name if it is not in a group
     */
    @NotNull
    public static String getGroupFromWorld(World world) {
        if (worldGroupCache.containsKey(world)) {
            return worldGroupCache.get(world);
        }

        String name = world.getName();

        for (String group : GROUPS.keySet()) {
            List<String> names = GROUPS.get(group);

            if (names == null) {
                worldGroupCache.put(world, name);
                return name;
            }

            for (String loopWorld : names) {
                if (name.equals(loopWorld)) {
                    worldGroupCache.put(world, group);
                    return group;
                }
            }
        }
        return name;
    }

    /**
     * Checks whether a specific type is shared by a world
     *
     * @param   world
     *          The world
     *
     * @param   type
     *          The type
     *
     * @return true if the world group of the world is sharing the provided type or if the group is null, false if not
     */
    public static boolean isSharing(World world, Type type) {
        String group = ConfigValue.getGroupFromWorld(world);

        if (group == null || !GROUPS_SHARE.containsKey(group)) {
            return true;
        }

        return GROUPS_SHARE.get(group).contains(type);
    }

    /**
     * Invalidates all caches
     */
    public static void invalidateCaches() {
        worldGroupCache.clear();
        groupWorldsCache.clear();
    }
}