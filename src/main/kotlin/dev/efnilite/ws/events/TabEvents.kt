package dev.efnilite.ws.events

import dev.efnilite.vilib.event.EventWatcher
import dev.efnilite.ws.WS
import dev.efnilite.ws.config.Config
import dev.efnilite.ws.player.WorldPlayer.Companion.asWorldPlayer
import dev.efnilite.ws.world.ShareType
import dev.efnilite.ws.world.World.Companion.asWorld
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.player.PlayerChangedWorldEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent


object TabEvents : EventWatcher {

    @EventHandler(priority = EventPriority.HIGH)
    fun join(event: PlayerJoinEvent) {
        if (!Config.CONFIG.getBoolean("tab")) {
            return
        }

        val player = event.player
        val inShared = player.asWorldPlayer().world.getPlayers(ShareType.TAB)

        for (other in Bukkit.getOnlinePlayers()) {
            if (other in inShared) {
                show(player, setOf(other))
            } else {
                hide(player, setOf(other))
            }
        }
    }

    @EventHandler
    fun quit(event: PlayerQuitEvent) {
        if (!Config.CONFIG.getBoolean("tab")) {
            return
        }

        show(event.player, Bukkit.getOnlinePlayers())
    }

    @EventHandler
    fun switchWorld(event: PlayerChangedWorldEvent) {
        if (!Config.CONFIG.getBoolean("tab")) {
            return
        }

        val player = event.player
        val from = event.from.asWorld()
        val to = player.world.asWorld()

        hide(player, from.getPlayers(ShareType.TAB))
        show(player, to.getPlayers(ShareType.TAB))
    }

    // hides player from others
    private fun hide(player: Player, others: Collection<Player>) {
        for (other in others) {
            WS.log("Hiding ${player.name} from ${other.name}")
            other.hidePlayer(WS.instance, player)
            player.hidePlayer(WS.instance, other)
        }
    }

    // shows player to others
    private fun show(player: Player, others: Collection<Player>) {
        for (other in others) {
            WS.log("Showing ${player.name} to ${other.name}")
            other.showPlayer(WS.instance, player)
            player.showPlayer(WS.instance, other)
        }
    }
}