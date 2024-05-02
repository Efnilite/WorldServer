package dev.efnilite.ws

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dev.efnilite.ws.config.Config
import dev.efnilite.vilib.ViPlugin
import dev.efnilite.vilib.util.Logging
import java.io.File

class WS : ViPlugin() {

    val logging = Logging(this)

    override fun enable() {
        instance = this
        stopping = false

        registerListener(Events)
        registerCommand("ws", Command)
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
        var stopping = false
            private set

        val GSON: Gson = GsonBuilder().disableHtmlEscaping().create()

        lateinit var instance: WS
            private set

        fun log(message: String) {
            if (Config.CONFIG.getBoolean("debug")) {
                instance.logging.info("[Debug] $message")
            }
        }
    }
}