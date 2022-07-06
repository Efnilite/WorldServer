package dev.efnilite.worldserver.hook;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PlaceholderHook {

    private static boolean papi;

    /**
     * Registers this PAPI hook
     */
    public static void register() {
        papi = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;
    }

    /**
     * If PAPI is found, translate placeholders. If not, return the given string.
     *
     * @param   player
     *          The player
     *
     * @param   string
     *          The string
     *
     * @return the string if PAPI is not found. the translated string if PAPI is found.
     */
    public static String translate(Player player, String string) {
        return papi ? PlaceholderAPI.setPlaceholders(player, string) : string;
    }
}
