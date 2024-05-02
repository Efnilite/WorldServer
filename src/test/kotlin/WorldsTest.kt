import dev.efnilite.ws.group.ShareType
import dev.efnilite.ws.group.Worlds
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class WorldsTest {

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