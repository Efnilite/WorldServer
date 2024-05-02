package dev.efnilite.ws

import dev.efnilite.vilib.event.EventWatcher
import dev.efnilite.ws.config.Config
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.player.PlayerChangedWorldEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent


object Events : EventWatcher {

    @EventHandler(priority = EventPriority.HIGH)
    fun join(event: PlayerJoinEvent) {
        if (!Config.CONFIG.getBoolean("tab")) {
            return
        }

        val player = event.player

//        val inGroup: List<Player> = GroupUtil.getPlayersInWorldGroup(player.world)
//
//        for (other in Bukkit.getOnlinePlayers()) {
//            if (inGroup.contains(other)) {
//                show(player, java.util.List.of(other))
//            } else {
//                hide(player, java.util.List.of(other))
//            }
//        }
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

//        hide(player, GroupUtil.getPlayersInWorldGroup(event.from)) // hide from previous world
//        show(player, GroupUtil.getPlayersInWorldGroup(player.world)) // show to current world
    }

    private fun hide(player: Player, others: Collection<Player>) {
        for (other in others) {
            other.hidePlayer(WS.instance, player)
            player.hidePlayer(WS.instance, other)
        }
    }

    private fun show(player: Player, others: Collection<Player>) {
        for (other in others) {
            other.showPlayer(WS.instance, player)
            player.showPlayer(WS.instance, other)
        }
    }
}