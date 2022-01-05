package dev.efnilite.worldserver.config;

import dev.efnilite.worldserver.WorldServer;
import org.bukkit.configuration.file.FileConfiguration;

public class Option {

    public static boolean VERBOSE;
    public static boolean CHAT_ENABLED;
    public static boolean TAB_ENABLED;
    public static boolean GLOBAL_CHAT_ENABLED;
    public static String GLOBAL_CHAT_PREFIX;
    public static String GLOBAL_CHAT_FORMAT;

    public static void init() {
        FileConfiguration config = WorldServer.getConfiguration().getFile("config");

        VERBOSE = config.getBoolean("verbose");
        CHAT_ENABLED = config.getBoolean("chat-enabled");
        TAB_ENABLED = config.getBoolean("tab-enabled");
        GLOBAL_CHAT_ENABLED = config.getBoolean("global-chat-enabled");
        GLOBAL_CHAT_PREFIX = config.getString("global-chat-prefix");
        GLOBAL_CHAT_FORMAT = config.getString("global-chat-format");
    }
}