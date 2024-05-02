package dev.efnilite.ws.player

import dev.efnilite.ws.world.World
import dev.efnilite.ws.world.Worlds
import org.bukkit.entity.Player
import java.util.*

class WorldPlayer(val player: Player) {

    var globalChat = false

    /**
     * This player's [World].
     */
    val world: World
        get() = Worlds.getWorld(player.world.name)

    companion object {

        val players = mutableMapOf<UUID, WorldPlayer>()

        fun Player.asWorldPlayer() = players[uniqueId]!!

    }
}