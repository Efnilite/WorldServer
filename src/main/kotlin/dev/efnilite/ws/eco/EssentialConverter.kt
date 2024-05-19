package dev.efnilite.ws.eco

import dev.efnilite.ws.WS
import dev.efnilite.ws.world.Shared
import org.bukkit.Bukkit
import org.bukkit.configuration.file.YamlConfiguration
import java.util.*

object EssentialConverter {

    fun convert(shared: Shared) {
        val essentials = Bukkit.getPluginManager().getPlugin("Essentials")!!
        val bals = mutableMapOf<UUID, Double>()

        essentials.dataFolder.resolve("userdata").listFiles()!!.forEach { file ->
            val uuid = UUID.fromString(file.nameWithoutExtension)
            val config = YamlConfiguration.loadConfiguration(file)

            bals[uuid] = config.getDouble("money")
        }

        WS.instance.dataFolder.resolve("players").listFiles()!!.forEach { file ->
            WS.gson.fromJson(file.reader(), EssentialsData::class.java).let {
                val uuid = UUID.fromString(file.nameWithoutExtension)

                it.balances[shared.name] = bals.getOrDefault(uuid, 0.0)

                WS.gson.toJson(it, file.writer())
            }
        }
    }
}

private data class EssentialsData(val balances: MutableMap<String, Double>, val globalChat: Boolean)