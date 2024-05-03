package dev.efnilite.ws.command

import dev.efnilite.vilib.command.ViCommand
import dev.efnilite.vilib.util.Strings
import dev.efnilite.ws.config.Config
import dev.efnilite.ws.world.Worlds
import org.bukkit.command.CommandSender

object Command : ViCommand() {

    override fun execute(sender: CommandSender, args: Array<out String>): Boolean {
        if (args.isEmpty()) {
            if (!sender.hasWPermission("ws")) {
                return true
            }

            with(sender) {
                send("")
                send("<dark_gray>= <gradient:#cc0066:#cc00bb><bold>WorldServer</bold></gradient> <dark_gray>=")
                send("")

                if (sender.hasWPermission("menu")) {
                    send("<#cc0066>/ws menu <dark_gray>- <gray>Opens the eco menu")
                }
                send("")

                if (sender.isOp) {
                    send("<#cc0066>/ws reload <dark_gray>- <gray>Reloads config files")
                    send("")
                }
            }
            return true
        }

        when (args[0].lowercase()) {
            "menu" -> {
                if (!sender.hasWPermission("menu")) {
                    return true
                }

                // Menu.open(sender)
            }
            "reload" -> {
                if (!sender.isOp) {
                    return true
                }

                Config.entries.forEach { it.load() }
                Worlds.init()

                sender.send("<#cc0066>Config files reloaded")
            }
        }
        return true
    }

    override fun tabComplete(sender: CommandSender, args: Array<out String>): List<String> {
        val suggestions = mutableListOf<String>()

        if (args.size == 1) {
            if (sender.hasWPermission("menu")) suggestions += "menu"
            if (sender.isOp) suggestions += "reload"
        }

        return suggestions
    }

    private fun CommandSender.send(message: String) = sendMessage(Strings.colour(message))

    fun CommandSender.hasWPermission(permission: String): Boolean {
        return if (Config.CONFIG.getBoolean("permissions")) {
            hasPermission(permission)
        } else {
            true
        }
    }
}