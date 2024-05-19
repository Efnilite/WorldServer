package dev.efnilite.ws.command

import dev.efnilite.vilib.command.ViCommand
import dev.efnilite.vilib.util.Strings
import dev.efnilite.ws.WorldPlayer.Companion.asWorldPlayer
import dev.efnilite.ws.command.Command.hasWPermission
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object PayCommand : ViCommand() {

    override fun execute(sender: CommandSender, args: Array<out String>): Boolean {
        if (sender !is Player || !sender.hasWPermission("ws.eco.pay")) return true

        if (args.isEmpty()) {
            sender.sendMessage(Strings.colour("<red>/pay <player> <amount>"))
        } else if (args.size == 1) {
            sender.sendMessage(Strings.colour("<red>/pay ${args[0]} <amount>"))
        }

        val to = Bukkit.getPlayer(args[0]) ?: return true
        val amount = args[1].toDoubleOrNull() ?: return true

        sender.asWorldPlayer().alterBalance(-amount)
        to.asWorldPlayer().alterBalance(amount)

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