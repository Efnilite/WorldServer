package dev.efnilite.ws.events

import dev.efnilite.vilib.event.EventWatcher
import dev.efnilite.vilib.util.Task
import dev.efnilite.ws.WS
import dev.efnilite.ws.config.Config
import dev.efnilite.ws.config.Locales
import dev.efnilite.ws.WorldPlayer.Companion.asWorldPlayer
import dev.efnilite.ws.world.ShareType
import dev.efnilite.ws.world.World.Companion.asWorld
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

object ChatEvents : EventWatcher {

    @EventHandler(priority = EventPriority.HIGH)
    fun chat(event: AsyncPlayerChatEvent) {
        if (!Config.CONFIG.getBoolean("chat")) {
            return
        }

        val player = event.player
        val message = event.message

        val prefix = Config.CONFIG.getString("global-chat-prefix")

        if (!Config.CONFIG.getBoolean("global-chat")) {
            event.recipients.clear()
            event.recipients.addAll(player.asWorldPlayer().world.getPlayers(ShareType.CHAT))
            return
        }

        if (player.asWorldPlayer().globalChat) {
            WS.log("Sending global chat from ${player.name}: $message")
        } else if (message.startsWith(prefix) && message.length > prefix.length) {
            event.message = message.substring(prefix.length)
        }

        // todo global format

        return
    }

    @EventHandler
    fun join(event: PlayerJoinEvent) {
        val player = event.player
        val world = player.world.asWorld()

        val sharedName = world.shared.firstOrNull { it.shareType == ShareType.CHAT }?.name
        val worldName = world.name

        val sharedFormat = Locales.getString(player, "chat-join-formats.$sharedName")
        val worldFormat = Locales.getString(player, "chat-join-formats.$worldName")

        val format = getFormat(sharedFormat, worldFormat, player) ?: return

        event.joinMessage = null

        Task.create(WS.instance)
            .delay(1)
            .execute {
                if (world.asWorld() != player.world) {
                    return@execute
                }

                world.getPlayers(ShareType.CHAT).forEach { it.sendMessage(format) }
            }.run()
    }

    @EventHandler
    fun quit(event: PlayerQuitEvent) {
        val player = event.player
        val world = player.world.asWorld()

        val sharedName = world.shared.firstOrNull { it.shareType == ShareType.CHAT }?.name
        val worldName = world.name

        val sharedFormat = Locales.getString(player, "chat-quit-formats.$sharedName")
        val worldFormat = Locales.getString(player, "chat-quit-formats.$worldName")

        val format = getFormat(sharedFormat, worldFormat, player) ?: return

        event.quitMessage = null

        world.getPlayers(ShareType.CHAT).forEach { it.sendMessage(format) }
    }

    private fun getFormat(sharedFormat: String, worldFormat: String, player: Player): String? {
        var format = when {
            sharedFormat.isNotEmpty() -> sharedFormat
            worldFormat.isNotEmpty() -> worldFormat
            else -> return null
        }

        if (WS.papiHook != null) {
            format = WS.papiHook!!.translate(player, format)
        }

        return format.replace("%player%", player.displayName)
    }
}