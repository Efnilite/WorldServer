package dev.efnilite.ws.hook

import me.clip.placeholderapi.PlaceholderAPI
import org.bukkit.Bukkit
import org.bukkit.entity.Player

object PapiHook {

    private var hasPapi: Boolean = false

    /**
     * Registers this PAPI hook
     */
    fun register() {
        hasPapi = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null
    }

    /**
     * If PAPI is found, translate placeholders. If not, return the given string.
     *
     * @param player The player
     * @param string The string
     * @return the string if PAPI is not found. the translated string if PAPI is found.
     */
    fun translate(player: Player, string: String): String {
        return if (hasPapi) PlaceholderAPI.setPlaceholders(player, string) else string
    }
}