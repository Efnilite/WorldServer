package dev.efnilite.ws.events

import dev.efnilite.vilib.event.EventWatcher
import dev.efnilite.ws.WorldPlayer.Companion.asWorldPlayer
import dev.efnilite.ws.config.Config
import dev.efnilite.ws.config.Locales
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.player.PlayerChangedWorldEvent
import org.bukkit.event.player.PlayerJoinEvent

object EcoEvents : EventWatcher {

    @EventHandler
    fun switchWorld(event: PlayerChangedWorldEvent) {
        handle(event.player)
    }

    @EventHandler(priority = EventPriority.LOW)
    fun join(event: PlayerJoinEvent) {
        handle(event.player)
    }

    private fun handle(player: Player) {
        if (!Config.CONFIG.getBoolean("eco.enabled") || !Config.CONFIG.getBoolean("eco.switch-notification")) {
            return
        }

        player.sendMessage(Locales.getString(player, "eco.switch")
            .replace("%amount%", player.asWorldPlayer().getBalance().toString()))
    }
}