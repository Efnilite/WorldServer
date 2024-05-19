package dev.efnilite.ws.world

import dev.efnilite.ws.WS
import dev.efnilite.ws.config.Config
import org.jetbrains.annotations.TestOnly

/**
 * Worlds handler.
 */
object Worlds {

    private val worlds = mutableMapOf<String, World>()

    fun init() {
        worlds.clear()

        populate(Config.CONFIG.getPaths("shared").associateWith { Config.CONFIG.getStringList("shared.$it") })
    }

    /**
     * Transforms the shared config options of the worlds into a map
     * where each [World] instance has the correct set of [Shared].
     *
     * @param sharedMap A map where the keys are
     */
    fun populate(sharedMap: Map<String, List<String>>) {
        for (sharedName in sharedMap.keys) {
            val inShared = mutableSetOf<World>()

            for (line in sharedMap[sharedName]!!) {
                val (name, shareTypes) = parseLine(line)
                val world = World(name, emptySet(), shareTypes)

                inShared += world
            }

            val map = mutableMapOf<World, Set<Shared>>()

            for (shareType in ShareType.entries) {
                val sharingWorlds = inShared.filter { shareType in it.shareTypes }
                    .toSet()

                for (world in sharingWorlds) {
                    map[world] = map.getOrDefault(world, emptySet()) + Shared(sharedName, shareType, sharingWorlds)
                }
            }

            for (world in inShared) {
                worlds[world.name] = world.copy(shared = map.getOrDefault(world, emptySet()))
            }
        }
    }

    // parses line into parts
    @TestOnly
    fun parseLine(line: String): Pair<String, Set<ShareType>> {
        val shareTypes = mutableSetOf<ShareType>()
        val elements = line.split("||")
            .map { it.lowercase().trim() }

        if ("chat" in elements) shareTypes.add(ShareType.CHAT)
        if ("tab" in elements) shareTypes.add(ShareType.TAB)
        if ("eco" in elements) shareTypes.add(ShareType.ECO)

        return Pair(elements.last(), shareTypes)
    }

    fun getWorld(name: String): World {
        val world = worlds.getOrDefault(name, World(name, emptySet(), emptySet()))

        if (name !in worlds) {
            worlds[name] = world
            WS.log("Registered late World as ${worlds[name]}")
        }

        return world
    }
}