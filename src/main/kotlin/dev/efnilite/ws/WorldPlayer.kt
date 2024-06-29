package dev.efnilite.ws

import com.google.gson.annotations.Expose
import dev.efnilite.ws.world.ShareType
import dev.efnilite.ws.world.Shared
import dev.efnilite.ws.world.World
import dev.efnilite.ws.world.Worlds
import org.bukkit.entity.Player
import java.util.*

class WorldPlayer private constructor(private val player: Player) {

    @Expose
    val balances = mutableMapOf<String, Double>()
    @Expose
    var globalChat = false

    /**
     * This player's [World].
     */
    val world: World
        get() = Worlds.getWorld(player.world.name)

    /**
     * Save this player's data to a file.
     */
    fun save() {
        val file = WS.instance.dataFolder.resolve("players/${player.uniqueId}.json")

        file.parentFile.mkdirs()
        file.createNewFile()

        file.writer().use { WS.gson.toJson(this, it) }
    }

    /**
     * Alter the balance of this player in the current [Shared].
     */
    fun alterBalance(amount: Double) {
        val shared = world.getShared(ShareType.ECO)?.name ?: return

        balances[shared] = getBalance() + amount
    }

    /**
     * Alter the balance of this player in the specified world.
     */
    fun alterBalance(worldName: String, amount: Double) {
        val shared = Worlds.getWorld(worldName).getShared(ShareType.ECO)?.name ?: return

        balances[shared] = getBalance() + amount
    }

    /**
     * Returns the balance of this player in the current [Shared].
     */
    fun getBalance(): Double {
        val shared = world.getShared(ShareType.ECO)?.name ?: return 0.0

        return balances.getOrDefault(shared, 0.0)
    }

    /**
     * Returns the balance of this player in the specified world.
     */
    fun getBalance(worldName: String): Double {
        val shared = Worlds.getWorld(worldName).getShared(ShareType.ECO)?.name ?: return 0.0

        return balances.getOrDefault(shared, 0.0)
    }

    fun send(message: String) {
        return player.sendMessage(message)
    }

    fun getPlayer() = player

    companion object {

        /**
         * Create a [WorldPlayer] from a [Player].
         */
        fun create(player: Player): WorldPlayer {
            WS.log("Added ${player.name}")

            val file = WS.instance.dataFolder.resolve("players/${player.uniqueId}.json")
            val wp = WorldPlayer(player)

            if (!file.exists()) return wp

            file.reader().use {
                try {
                    val from = WS.gson.fromJson(it, WorldPlayer::class.java)

                    wp.balances.putAll(from.balances)
                    wp.globalChat = from.globalChat
                } catch (ex: Exception) {
                    WS.instance.logging.stack("Failed to load player data for ${player.name}", ex)
                }
            }

            return wp
        }

        val players = mutableMapOf<UUID, WorldPlayer>()

        fun Player.asWorldPlayer() = players.getOrPut(uniqueId) { create(this) }
    }
}