package dev.efnilite.ws

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dev.efnilite.vilib.ViPlugin
import dev.efnilite.vilib.bstats.bukkit.Metrics
import dev.efnilite.vilib.bstats.charts.SimplePie
import dev.efnilite.vilib.util.Logging
import dev.efnilite.vilib.util.UpdateChecker
import dev.efnilite.ws.command.*
import dev.efnilite.ws.config.Config
import dev.efnilite.ws.config.Locales
import dev.efnilite.ws.eco.Provider
import dev.efnilite.ws.events.ChatEvents
import dev.efnilite.ws.events.EcoEvents
import dev.efnilite.ws.events.TabEvents
import dev.efnilite.ws.hook.PapiHook
import dev.efnilite.ws.world.Worlds
import net.milkbowl.vault.economy.Economy
import org.bukkit.Bukkit
import org.bukkit.plugin.ServicePriority
import java.io.File

class WS : ViPlugin() {

    val logging = Logging(this)

    override fun onLoad() {
        instance = this
        stopping = false

        if (Bukkit.getPluginManager().isPluginEnabled("Vault") &&
            Config.CONFIG.getBoolean("eco.enabled")) {
            try {
                Class.forName("net.milkbowl.vault.economy.Economy")
                server.servicesManager.register(Economy::class.java, Provider, this, ServicePriority.High)
                logging.info("Registered with Vault!");
            } catch (ignored: ClassNotFoundException) {

            } catch (ignored: NoClassDefFoundError) {

            }
        }
    }

    override fun enable() {
        Locales.init()
        Worlds.init()

        registerListener(Events)
        registerListener(ChatEvents)
        registerListener(EcoEvents)
        registerListener(TabEvents)

        registerCommand("ws", Command)
        registerCommand("globalchat", GcCommand)

        if (Config.CONFIG.getBoolean("eco.enabled")) {
            registerCommand("balance", BalCommand)
            registerCommand("balancetop", TopCommand)
            registerCommand("pay", PayCommand)
        }

        if (server.pluginManager.isPluginEnabled("PlaceholderAPI")) {
            log("Registered PlaceholderAPI Hook")
            papiHook = PapiHook
        }

        val metrics = Metrics(this, 13856)
        metrics.addCustomChart(SimplePie("chat_enabled") { Config.CONFIG.getBoolean("chat").toString() })
        metrics.addCustomChart(SimplePie("tab_enabled") { Config.CONFIG.getBoolean("tab").toString() })
        metrics.addCustomChart(SimplePie("eco_enabled") { Config.CONFIG.getBoolean("eco.enabled").toString() })

        UpdateChecker.check(this, 99010)
    }

    override fun disable() {
        stopping = true

        WorldPlayer.players.values.forEach { it.save() }
        WorldPlayer.players.clear()
    }

    fun saveFile(path: String) {
        val file = File(dataFolder.toString(), path)

        if (!file.exists()) {
            saveResource(path, false)
        }
    }

    companion object {
        var papiHook: PapiHook? = null

        val gson: Gson = GsonBuilder().disableHtmlEscaping().excludeFieldsWithoutExposeAnnotation().create()

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