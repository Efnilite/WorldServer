package dev.efnilite.ws

import dev.efnilite.vilib.event.EventWatcher
import dev.efnilite.ws.WorldPlayer.Companion.asWorldPlayer
import dev.efnilite.ws.WorldPlayer.Companion.players
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

object Events : EventWatcher {

    @EventHandler
    fun join(event: PlayerJoinEvent) {
        val player = event.player

        WS.log("Added ${player.name}")

        WorldPlayer.create(player)
    }

    @EventHandler
    fun quit(event: PlayerQuitEvent) {
        val player = event.player

        WS.log("Removed ${player.name}")

        player.asWorldPlayer().save()
        players.remove(player.uniqueId)
    }

}