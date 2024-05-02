package dev.efnilite.ws

import dev.efnilite.vilib.ViPlugin
import dev.efnilite.vilib.util.Logging
import dev.efnilite.ws.command.Command
import dev.efnilite.ws.command.GcCommand
import dev.efnilite.ws.config.Config
import dev.efnilite.ws.config.Locales
import dev.efnilite.ws.events.ChatEvents
import dev.efnilite.ws.events.EcoEvents
import dev.efnilite.ws.events.TabEvents
import dev.efnilite.ws.hook.PapiHook
import java.io.File

class WS : ViPlugin() {

    val logging = Logging(this)

    override fun enable() {
        instance = this
        stopping = false

        Locales.init()

        registerListener(Events)
        registerListener(ChatEvents)
        registerListener(EcoEvents)
        registerListener(TabEvents)

        registerCommand("ws", Command)
        registerCommand("globalchat", GcCommand)

        if (server.pluginManager.isPluginEnabled("PlaceholderAPI")) {
            log("Registered PlaceholderAPI Hook")
            papiHook = PapiHook
            PapiHook.register()
        }
    }

    override fun disable() {
        stopping = true
    }

    fun saveFile(path: String) {
        val file = File(dataFolder.toString(), path)

        if (!file.exists()) {
            saveResource(path, false)
        }
    }

    companion object {
        var papiHook: PapiHook? = null

        var stopping = false
            private set

        lateinit var instance: WS
            private set

        fun log(message: String) {
            if (Config.CONFIG.getBoolean("debug")) {
                instance.logging.info("[Debug] $message")
            }
        }
    }
}