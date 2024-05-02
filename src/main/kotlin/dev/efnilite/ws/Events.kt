package dev.efnilite.ws

import dev.efnilite.vilib.event.EventWatcher
import dev.efnilite.ws.WorldPlayer.Companion.players
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

object Events : EventWatcher {

    @EventHandler
    fun join(event: PlayerJoinEvent) {
        WS.log("Added ${event.player.name}")

        players[event.player.uniqueId] = WorldPlayer(event.player)
    }

    @EventHandler
    fun quit(event: PlayerQuitEvent) {
        WS.log("Removed ${event.player.name}")

        players.remove(event.player.uniqueId)
    }

}