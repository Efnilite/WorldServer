package dev.efnilite.worldserver.config;

import org.bukkit.plugin.Plugin;

import java.util.logging.Logger;

/**
 * Adds useful data for later (e.g. game testing)
 */
public class Verbose {

    private static Logger logger;

    public static void init(Plugin plugin) {
        logger = plugin.getLogger();
    }

    public static void info(String info) {
        logger.info(info);
    }

    public static void warn(String warn) {
        logger.warning(warn);
    }

    public static void error(String error) {
        logger.severe(error);
    }
}
