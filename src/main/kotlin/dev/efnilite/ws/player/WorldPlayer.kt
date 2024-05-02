package dev.efnilite.ws.player

import dev.efnilite.ws.world.World
import dev.efnilite.ws.world.Worlds
import org.bukkit.entity.Player

class WorldPlayer(val player: Player) {

    /**
     * This player's [World].
     */
    val world: World
        get() = Worlds.getWorld(player.world.name)

    companion object {

        fun Player.asWorldPlayer() = WorldPlayer(this)

    }

}