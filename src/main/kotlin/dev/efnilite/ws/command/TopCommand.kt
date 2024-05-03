package dev.efnilite.ws.command

import dev.efnilite.vilib.command.ViCommand
import dev.efnilite.ws.command.Command.hasWPermission
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object TopCommand : ViCommand() {

    override fun execute(sender: CommandSender, args: Array<out String>): Boolean {
        if (sender !is Player || !sender.hasWPermission("ws.eco.baltop")) return true

        return true
    }

    override fun tabComplete(sender: CommandSender, args: Array<out String>) = mutableListOf<String>()
}