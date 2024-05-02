package dev.efnilite.ws.events

import dev.efnilite.vilib.event.EventWatcher
import dev.efnilite.ws.WS
import dev.efnilite.ws.config.Config
import dev.efnilite.ws.player.WorldPlayer.Companion.asWorldPlayer
import dev.efnilite.ws.world.ShareType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.player.AsyncPlayerChatEvent

object ChatEvents : EventWatcher {

    @EventHandler(priority = EventPriority.HIGH)
    fun chat(event: AsyncPlayerChatEvent) {
        if (!Config.CONFIG.getBoolean("chat")) {
            return
        }

        val player = event.player
        val message = event.message

        val prefix = Config.CONFIG.getString("global-chat-prefix")
        if (useGlobalChat(player, message, prefix)) {
            event.message = message.substring(prefix.length)

            WS.log("Sending global chat from ${player.name}: $message")
        }

        event.recipients.clear()
        event.recipients.addAll(player.asWorldPlayer().world.getPlayers(ShareType.CHAT))
    }

    private fun useGlobalChat(player: Player, message: String, prefix: String): Boolean {
        if (!Config.CONFIG.getBoolean("global-chat")) return false

        return player.asWorldPlayer().globalChat || (message.startsWith(prefix) && message.length > prefix.length)
    }

}