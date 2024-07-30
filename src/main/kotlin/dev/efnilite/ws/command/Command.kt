package dev.efnilite.ws.command

import dev.efnilite.vilib.command.ViCommand
import dev.efnilite.vilib.util.Strings
import dev.efnilite.vilib.util.Task
import dev.efnilite.ws.WS
import dev.efnilite.ws.config.Config
import dev.efnilite.ws.eco.EssentialConverter
import dev.efnilite.ws.world.ShareType
import dev.efnilite.ws.world.Worlds
import org.bukkit.Bukkit
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
                    send("<#cc0066>/ws migrate <group> <dark_gray>- <gray>Migrate Essentials economy to WorldServer economy")
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
            "migrate" -> {
                if (!sender.isOp) {
                    return true
                }

                if (args.size != 2) {
                    sender.send("<#cc0066>/ws migrate <group>")
                    return true
                }
                if (Bukkit.getOnlinePlayers().isNotEmpty()) {
                    sender.send("<#cc0066>To prevent concurrency issues, you can only migrate the economy when no players are online.")
                    return true
                }

                val world = Worlds.getWorld(args[1])
                val group = world.getShared(ShareType.ECO)

                if (group == null) {
                    sender.send("<#cc0066>Group or world ${args[1]} does not share economy.")
                    return true
                }

                Task.create(WS.instance).async()
                    .execute {
                        try {
                            EssentialConverter.convert(group)
                            sender.send("<#cc0066>All balances from Essentials have been copied to the ${group.name} group.")
                        } catch (ex: Exception) {
                            WS.instance.logging.stack("Failed to migrate Essentials economy", ex)
                        }
                    }
                    .run()
            }
        }
        return true
    }

    override fun tabComplete(sender: CommandSender, args: Array<out String>): List<String> {
        val suggestions = mutableListOf<String>()

        when (args.size) {
            1 -> {
                if (sender.hasWPermission("menu")) suggestions += "menu"
                if (sender.isOp) suggestions += "reload"
                if (sender.isOp) suggestions += "migrate"
            }
            2 -> {
                if (args[0].lowercase() == "migrate" && sender.isOp) {
                    suggestions += Worlds.getWorlds()
                }
            }
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