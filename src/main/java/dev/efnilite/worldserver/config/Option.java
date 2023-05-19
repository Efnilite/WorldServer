package dev.efnilite.worldserver.config;

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
        AUTO_UPDATER = Config.CONFIG.getBoolean("auto-updater");

        GROUPS = new HashMap<>();
        Config.CONFIG.getChildren("groups", false)
                .forEach(group -> GROUPS.put(group, Config.CONFIG.getStringList("groups." + group)));

        TAB_ENABLED = Config.CONFIG.getBoolean("tab-enabled");

        CHAT_ENABLED = Config.CONFIG.getBoolean("chat-enabled");
        GLOBAL_CHAT_ENABLED = Config.CONFIG.getBoolean("global-chat-enabled");
        GLOBAL_CHAT_PREFIX = Config.CONFIG.getString("global-chat-prefix");
        GLOBAL_CHAT_FORMAT = Config.CONFIG.getString("global-chat-format");
        SPY_FORMAT = Config.CONFIG.getString("spy-format");

        CHAT_FORMAT = new HashMap<>();
        Config.CONFIG.getChildren("chat-format", false)
                .forEach(world -> CHAT_FORMAT.put(world, Config.CONFIG.getString("chat-format." + world)));

        CHAT_COOLDOWN = new HashMap<>();
        Config.CONFIG.getChildren("chat-cooldown", false)
                .forEach(world -> CHAT_COOLDOWN.put(world, Config.CONFIG.getDouble("chat-cooldown." + world)));

        CHAT_COOLDOWN_FORMAT = Config.CONFIG.getString("chat-cooldown-format");
        CHAT_BLOCKED = Config.CONFIG.getStringList("chat-blocked");
        CHAT_BLOCKED_FORMAT = Config.CONFIG.getString("chat-blocked-format");

        CHAT_JOIN_FORMATS = new HashMap<>();

        Config.CONFIG.getChildren("chat-join-formats", false)
                .forEach(world -> CHAT_JOIN_FORMATS.put(world, Config.CONFIG.getString("chat-join-formats." + world)));

        CHAT_LEAVE_FORMATS = new HashMap<>();
        Config.CONFIG.getChildren("chat-leave-formats", false)
                .forEach(world -> CHAT_LEAVE_FORMATS.put(world, Config.CONFIG.getString("chat-leave-formats." + world)));

        CLEAR_CHAT_ON_SWITCH = Config.CONFIG.getBoolean("clear-chat-on-switch");

        ECONOMY_ENABLED = Config.CONFIG.getBoolean("economy-enabled");
        ECONOMY_GLOBAL_ENABLED = Config.CONFIG.getBoolean("economy-global-enabled");
        ECONOMY_SWITCH_NOTIFICATION = Config.CONFIG.getBoolean("economy-switch-notification");
        ECONOMY_SWITCH_FORMAT = Config.CONFIG.getString("economy-switch-format");
        ECONOMY_CURRENCY_SYMBOL = Config.CONFIG.getString("economy-currency-symbol");
        ECONOMY_CURRENCY_NAMES = Config.CONFIG.getStringList("economy-currency-names");

        ECONOMY_STARTING_AMOUNT = new HashMap<>();
        Config.CONFIG.getChildren("economy-starting-amount", false)
                .forEach(world -> ECONOMY_STARTING_AMOUNT.put(world, Config.CONFIG.getDouble("economy-starting-amount." + world)));

        ECONOMY_OVERRIDE_BALANCE_COMMAND = Config.CONFIG.getBoolean("economy-override-balance-command");
        ECONOMY_BALANCE_FORMAT = Config.CONFIG.getString("economy-balance-format");
        ECONOMY_BALANCE_CHANGE = Config.CONFIG.getBoolean("economy-balance-change");
        ECONOMY_BALANCE_CHANGE_FORMAT = Config.CONFIG.getString("economy-balance-change-format");
        ECONOMY_OVERRIDE_PAY_COMMAND = Config.CONFIG.getBoolean("economy-override-pay-command");
        ECONOMY_PAY_NO_FUNDS_FORMAT = Config.CONFIG.getString("economy-pay-no-funds-format");
        ECONOMY_PAY_SEND_FORMAT = Config.CONFIG.getString("economy-pay-send-format");
        ECONOMY_PAY_RECEIVE_FORMAT = Config.CONFIG.getString("economy-pay-receive-format");
        ECONOMY_OVERRIDE_BALTOP_COMMAND = Config.CONFIG.getBoolean("economy-override-baltop-command");
    }
}