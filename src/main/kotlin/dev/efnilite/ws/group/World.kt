package dev.efnilite.ws.group

import org.bukkit.Bukkit
import org.bukkit.World

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

    fun asWorld(): World = Bukkit.getWorld(name)!!

}