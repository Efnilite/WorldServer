package dev.efnilite.ws.hook;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PlaceholderHook {

    private static boolean hasPapi;

    /**
     * Registers this PAPI hook
     */
    public static void register() {
        hasPapi = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;
    }

    /**
     * If PAPI is found, translate placeholders. If not, return the given string.
     *
     * @param player The player
     * @param string The string
     * @return the string if PAPI is not found. the translated string if PAPI is found.
     */
    public static String translate(Player player, String string) {
        return hasPapi ? PlaceholderAPI.setPlaceholders(player, string) : string;
    }
}
