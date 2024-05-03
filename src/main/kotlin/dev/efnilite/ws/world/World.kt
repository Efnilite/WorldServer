package dev.efnilite.ws.world

import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.entity.Player

/**
 * Represents a world.
 *
 * @param name The name of the world.
 * @param shared The set of shared sets that contain this world.
 * @param shareTypes The shared values of the world.
 */
data class World(val name: String,
                 val shared: Set<Shared>,
                 val shareTypes: Set<ShareType>) {

    /**
     * Returns this world as a [World] instance.
     */
    fun asWorld(): World? = Bukkit.getWorld(name)

    /**
     * Returns the [Shared] with the specified [ShareType].
     */
    fun getShared(shareType: ShareType) = shared.firstOrNull { it.shareType == shareType }

    /**
     * Returns the players in [Shared] with the specified [ShareType].
     */
    fun getPlayers(shareType: ShareType): Set<Player> {
        val sharedWithType = getShared(shareType) ?: return asWorld()?.players?.toSet() ?: emptySet()

        return sharedWithType.worlds
            .mapNotNull { it.asWorld() }
            .flatMap { it.players }
            .toSet()
    }

    companion object {

        fun World.asWorld() = Worlds.getWorld(name)

    }
}