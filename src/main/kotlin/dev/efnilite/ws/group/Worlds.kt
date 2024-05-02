package dev.efnilite.ws.group

import dev.efnilite.ws.config.Config
import org.jetbrains.annotations.TestOnly

object Worlds {

    private val worlds = mutableMapOf<String, World>()

    /**
     * Transforms the shared config options of the worlds into a map
     * where each [World] instance has the correct set of [Shared].
     */
    // todo tests
    fun init() {
        val shared = Config.CONFIG.getPaths("shared")

        for (sharedName in shared) {
            val inShared = mutableSetOf<World>()

            for (line in Config.CONFIG.getStringList("shared.$sharedName")) {
                val (name, shareTypes) = parseLine(line)
                val world = World(name, emptySet(), shareTypes)

                inShared += world
            }

            val map = mutableMapOf<World, Set<Shared>>()

            for (shareType in ShareType.entries) {
                val sharingWorlds = inShared.filter { it.shareTypes.contains(shareType) }
                    .toSet()

                for (world in sharingWorlds) {
                    map[world] = map.getOrDefault(world, emptySet()) + Shared(shareType, sharingWorlds)
                }
            }

            for (world in inShared) {
                worlds[world.name] = world.copy(shared = map.getOrDefault(world, emptySet()))
            }
        }
    }

    @TestOnly
    fun parseLine(line: String): Pair<String, Set<ShareType>> {
        val shareTypes = mutableSetOf<ShareType>()
        val elements = line.split("||")
            .map { it.lowercase().trim() }

        if (elements.contains("chat")) shareTypes.add(ShareType.CHAT)
        if (elements.contains("tab")) shareTypes.add(ShareType.TAB)
        if (elements.contains("eco")) shareTypes.add(ShareType.ECO)

        return Pair(elements.last(), shareTypes)
    }
}