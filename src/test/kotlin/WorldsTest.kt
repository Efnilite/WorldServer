import dev.efnilite.ws.world.ShareType
import dev.efnilite.ws.world.Shared
import dev.efnilite.ws.world.Worlds
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class WorldsTest {

    @Test
    fun testPopulate() {
        mapOf(
            "lobby" to listOf("world", "chat||tab||world_nether", "chat||world_the_end"),
            "factions" to listOf("chat||tab||eco||factions", "chat||tab||eco||factions_nether", "chat||tab||eco||factions_the_end")
        ).let { sharedMap ->
            Worlds.populate(sharedMap)
        }

        // test no overlap
        Worlds.getWorld("world").let { world ->
            assertEquals("world", world.name)
            assertEquals(emptySet<Set<Shared>>(), world.shared)
            assertEquals(emptySet<ShareType>(), world.shareTypes)
        }

        // test some overlap
        Worlds.getWorld("world_nether").let { world ->
            assertEquals("world_nether", world.name)

            assertEquals(setOf(
                setOf("world_nether", "world_the_end"),
                setOf("world_nether")
            ), world.shared.map { w -> w.worlds.map { it.name }.toSet() }.toSet())

            assertEquals(setOf(
                ShareType.CHAT,
                ShareType.TAB
            ), world.shared.map { it.shareType }.toSet())

            assertEquals(setOf(ShareType.CHAT, ShareType.TAB), world.shareTypes)
        }

        // test full overlap
        Worlds.getWorld("factions_the_end").let { world ->
            assertEquals("factions_the_end", world.name)

            assertEquals(setOf(
                setOf("factions", "factions_nether", "factions_the_end"),
                setOf("factions", "factions_nether", "factions_the_end"),
                setOf("factions", "factions_nether", "factions_the_end"),
            ), world.shared.map { w -> w.worlds.map { it.name }.toSet() }.toSet())

            assertEquals(setOf(
                ShareType.CHAT,
                ShareType.TAB,
                ShareType.ECO
            ), world.shared.map { it.shareType }.toSet())

            assertEquals(setOf(ShareType.CHAT, ShareType.TAB, ShareType.ECO), world.shareTypes)
        }
    }

    @Test
    fun testParseLine() {
        // test none
        Worlds.parseLine("world").let { (name, shareTypes) ->
            assertEquals("world", name)
            assertEquals(emptySet<ShareType>(), shareTypes)
        }

        // test all
        Worlds.parseLine("chat||tab||eco||world").let { (name, shareTypes) ->
            assertEquals("world", name)
            assertEquals(setOf(ShareType.CHAT, ShareType.TAB, ShareType.ECO), shareTypes)
        }

        // test misspelling
        Worlds.parseLine("chat||leco||world").let { (name, shareTypes) ->
            assertEquals("world", name)
            assertEquals(setOf(ShareType.CHAT), shareTypes)
        }

        // test order
        Worlds.parseLine("tab||chat||world").let { (name, shareTypes) ->
            assertEquals("world", name)
            assertEquals(setOf(ShareType.CHAT, ShareType.TAB), shareTypes)
        }
    }
}