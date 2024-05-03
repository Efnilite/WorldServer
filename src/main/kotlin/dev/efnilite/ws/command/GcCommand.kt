package dev.efnilite.ws.command

import dev.efnilite.vilib.command.ViCommand
import dev.efnilite.ws.config.Locales
import dev.efnilite.ws.WorldPlayer.Companion.asWorldPlayer
import dev.efnilite.ws.command.Command.hasWPermission
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object GcCommand : ViCommand() {

    override fun execute(sender: CommandSender, args: Array<out String>): Boolean {
        if (sender !is Player || !sender.hasWPermission("ws.global")) return true

        val player = sender.asWorldPlayer()
        player.globalChat = !player.globalChat

        sender.sendMessage(
            if (player.globalChat) {
                Locales.getString(sender, "global-chat.enable")
            } else {
                Locales.getString(sender, "global-chat.disable")
            }
        )

        return true
    }

    override fun tabComplete(sender: CommandSender, args: Array<out String>) = emptyList<String>()
}