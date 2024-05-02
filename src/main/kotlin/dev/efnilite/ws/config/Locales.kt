package dev.efnilite.ws.config

import dev.efnilite.vilib.inventory.item.Item
import dev.efnilite.vilib.util.Strings
import dev.efnilite.vilib.util.Task
import dev.efnilite.ws.WS
import org.bukkit.Material
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import java.util.function.Function
import java.util.regex.Pattern

/**
 * Locale message/item handler.
 */
object Locales {

    // a list of all nodes, used to check against missing nodes
    private var resourceNodes = emptyList<String>()

    /**
     * A map of all locales with their respective yml trees
     */
    private val locales: LinkedHashMap<String, FileConfiguration> = LinkedHashMap()

    fun getLocales() = locales.keys

    /**
     * Initializes this Locale handler.
     */
    fun init() {
        val plugin = WS.instance

        Task.create(plugin).async().execute {
            locales.clear()
            val embedded: FileConfiguration = YamlConfiguration.loadConfiguration(
                InputStreamReader(
                    plugin.getResource("locales/en_us.yml")!!,
                    StandardCharsets.UTF_8
                )
            )

            // get all nodes from the plugin's english resource, aka the most updated version
            resourceNodes = getChildren(embedded)

            val folder: File = plugin.dataFolder.resolve("locales")

            // download files to locales folder
            if (!folder.exists()) {
                folder.mkdirs()
            }

            val files = folder.list()

            // create non-existent files
            if (files != null && files.isEmpty()) {
                plugin.saveResource("locales/en_us.yml", false)
                plugin.saveResource("locales/nl_nl.yml", false)
            }

            // get all files in locales folder
            try {
                Files.list(folder.toPath()).use { stream ->
                    stream.forEach { path: Path ->
                        val file = path.toFile()
                        // get locale from file name
                        val locale = file.name.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]

                        WS.log("Locale $locale found")

                        val config: FileConfiguration = YamlConfiguration.loadConfiguration(file)

                        validate(embedded, config, file)

                        locales[locale] = config
                    }
                }
            } catch (ex: Exception) {
                plugin.logging.stack("Error while trying to read locale files", "restart/reload your server", ex)
            }
        }.run()
    }

    // validates whether a lang file contains all required keys.
    // if it doesn't, automatically add them
    private fun validate(provided: FileConfiguration, user: FileConfiguration, localPath: File) {
        val userNodes: List<String> = getChildren(user)

        for (node in resourceNodes) {
            if (node in userNodes) {
                continue
            }

            WS.log("Fixing missing config node $node (${localPath.name})")

            user.set(node, provided.get(node))
        }

        try {
            user.save(localPath)

            WS.log("Validated locale file ${localPath.name}")
        } catch (ex: IOException) {
            WS.instance.logging.stack(
                "Error while trying to save fixed config file $localPath",
                "delete this file and restart your server", ex
            )
        }
    }

    fun getString(player: Player, path: String): String {
        return getString(player.locale.lowercase(), path)
    }

    private fun getString(locale: String, path: String): String {
        return Strings.colour(
            getValue(
            locale,
            Function<FileConfiguration, String> { config: FileConfiguration -> config.getString(path) },
            "")
        )
    }

    fun getStringList(player: Player, path: String): List<String> {
        return getStringList(player.locale.lowercase(), path)
    }

    private fun getStringList(locale: String, path: String): List<String> {
        return getValue(
            locale,
            { config: FileConfiguration -> config.getStringList(path) },
            emptyList()
        ).map { Strings.colour(it) }
    }

    private fun <T> getValue(locale: String, f: Function<FileConfiguration, T>, def: T): T {
        if (locales.isEmpty()) return def

        val config = (locales[locale] ?: locales[Config.CONFIG.getString("locale")]) ?: return def

        return f.apply(config) ?: def
    }

    fun getItem(player: Player, path: String, vararg replace: String): Item {
        return getItem(player.locale.lowercase(), path, *replace)
    }

    private val pattern: Pattern = Pattern.compile("%[a-z]")

    private fun getItem(locale: String, path: String, vararg replace: String): Item {
        if (locales.isEmpty()) { // during reloading
            return Item(Material.STONE, "")
        }
        if (locales[locale] == null) {
            WS.instance.logging.error("Invalid locale $locale")
            return Item(Material.STONE, "")
        }

        val base: FileConfiguration = locales[locale]!!

        val material = base.getString("$path.material") ?: ""
        var name = base.getString("$path.name") ?: ""
        var lore = base.getString("$path.lore") ?: ""

        var idx = 0
        var matcher = pattern.matcher(name)
        while (matcher.find()) {
            if (idx == replace.size) {
                break
            }

            name = name.replaceFirst(matcher.group().toRegex(), replace[idx])
            idx++
        }

        matcher = pattern.matcher(lore)

        while (matcher.find()) {
            if (idx == replace.size) {
                break
            }

            lore = lore.replaceFirst(matcher.group().toRegex(), replace[idx])
            idx++
        }

        val item = Item(Material.getMaterial(material.uppercase(Locale.getDefault())), name)

        if (lore.isNotEmpty()) {
            item.lore(*lore.split("\\|\\|".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray())
        }

        return item
    }

    private fun getChildren(file: FileConfiguration): List<String> {
        return file.getConfigurationSection("")?.getKeys(true)?.toList() ?: mutableListOf()
    }
}