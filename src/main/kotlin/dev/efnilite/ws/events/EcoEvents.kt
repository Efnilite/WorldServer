package dev.efnilite.ws.events

import dev.efnilite.vilib.event.EventWatcher
import dev.efnilite.ws.WorldPlayer
import dev.efnilite.ws.config.Config
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.player.PlayerChangedWorldEvent
import org.bukkit.event.player.PlayerJoinEvent


object EcoEvents : EventWatcher {

    @EventHandler
    fun switchWorld(event: PlayerChangedWorldEvent) {
        if (!Config.CONFIG.getBoolean("eco.enabled") || !Config.CONFIG.getBoolean("eco.switch-notification")) {
            return
        }

        val player: WorldPlayer = WorldPlayer.player

        player.send(Option.ECONOMY_SWITCH_FORMAT.replace("%amount%", player.getBalance().toString()))
    }

    @EventHandler(priority = EventPriority.LOW)
    fun join(event: PlayerJoinEvent) {
        if (!Config.CONFIG.getBoolean("eco.enabled") || !Config.CONFIG.getBoolean("eco.switch-notification")) {
            return
        }

        val player: WorldPlayer = WorldPlayer.player

        player.send(Option.ECONOMY_SWITCH_FORMAT.replace("%amount%", player.getBalance().toString()))
    }

}