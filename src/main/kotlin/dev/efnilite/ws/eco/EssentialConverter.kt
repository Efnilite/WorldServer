package dev.efnilite.ws.eco

import dev.efnilite.ws.WS
import dev.efnilite.ws.world.Shared
import org.bukkit.Bukkit
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.io.InputStreamReader
import java.util.*

object EssentialConverter {

    fun convert(shared: Shared) {
        val bals = mutableMapOf<UUID, Double>()

        File("plugins/Essentials").resolve("userdata").listFiles()!!.forEach { file ->
            val uuid = UUID.fromString(file.nameWithoutExtension)
            val config = YamlConfiguration.loadConfiguration(file)

            bals[uuid] = config.getDouble("money")
        }

        WS.instance.dataFolder.resolve("players").listFiles()!!.forEach { file ->
            file.reader().use { reader ->
                WS.gson.fromJson(reader, EssentialsData::class.java).let {
                    val uuid = UUID.fromString(file.nameWithoutExtension)

                    val map = it.balances ?: mutableMapOf()

                    map[shared.name] = bals.getOrDefault(uuid, 0.0)

                    it.balances = map

                    file.writer().use { writer -> WS.gson.toJson(it, writer) }
                }
            }
        }
    }
}

private data class EssentialsData(var balances: MutableMap<String, Double>?, val globalChat: Boolean)