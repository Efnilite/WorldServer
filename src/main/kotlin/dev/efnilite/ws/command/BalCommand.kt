package dev.efnilite.ws.command

import dev.efnilite.vilib.command.ViCommand
import dev.efnilite.ws.WorldPlayer.Companion.asWorldPlayer
import dev.efnilite.ws.command.Command.hasWPermission
import dev.efnilite.ws.config.Locales
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object BalCommand : ViCommand() {

    override fun execute(sender: CommandSender, args: Array<out String>): Boolean {
        if (sender !is Player || !sender.hasWPermission("ws.eco.bal")) return true

        when (args.size) {
            0 -> sender.sendMessage(Locales.getString(sender, "eco.bal.you")
                .replace("%amount%", sender.asWorldPlayer().getBalance().toString()))
            else -> {
                val other = Bukkit.getPlayer(args[0])

                if (other == null) {
                    sender.sendMessage(Locales.getString(sender, "eco.bal.not-found")
                        .replace("%player%", args[0]))
                    return true
                }

                sender.sendMessage(Locales.getString(sender, "eco.bal.other")
                    .replace("%player%", other.name)
                    .replace("%amount%", other.asWorldPlayer().getBalance().toString()))
            }
        }

        return true
    }

    override fun tabComplete(sender: CommandSender, args: Array<out String>): MutableList<String> {
        val suggestions = mutableListOf<String>()

        if (args.size == 1) {
            suggestions.addAll(Bukkit.getOnlinePlayers().map { it.name })
        }

        return suggestions
    }
}