package dev.efnilite.ws

import dev.efnilite.vilib.event.EventWatcher
import dev.efnilite.vilib.util.Task
import dev.efnilite.ws.WorldPlayer.Companion.asWorldPlayer
import dev.efnilite.ws.WorldPlayer.Companion.players
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerQuitEvent

object Events : EventWatcher {

    @EventHandler
    fun quit(event: PlayerQuitEvent) {
        val player = event.player

        WS.log("Removed ${player.name}")

        if (WS.stopping) {
            player.asWorldPlayer().save()
            players.remove(player.uniqueId)
        } else {
            Task.create(WS.instance)
                .async()
                .execute {
                    player.asWorldPlayer().save()
                    players.remove(player.uniqueId)
                }
                .run()
        }
    }
}