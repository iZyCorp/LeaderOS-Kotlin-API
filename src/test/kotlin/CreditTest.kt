import io.github.cdimascio.dotenv.dotenv
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class CreditTest {

    val leaderOS: LeaderOS = LeaderOS(dotenv()["BASE_URL"], dotenv()["API_KEY"])

    @Test
    fun show() = runTest {
        val result = leaderOS.credits.show("Dremy_115")

        assertNotNull(result)
        assertEquals("\"EUR\"", result["currency"].toString())
    }

    @Test
    fun add() = runTest {
        val result = leaderOS.credits.add("iZyy_", 10.0)

        assertNotNull(result)
        assertEquals("true", result["status"].toString())
    }

    @Test
    fun remove() = runTest {
        val result = leaderOS.credits.oldRemove("iZyy_", 10.0)

        assertNotNull(result)
        assertEquals("true", result["status"].toString())
    }

    @Test
    fun send() = runTest {
        val result = leaderOS.credits.send("Dremy_115", "iZyy_", 10.0)

        assertNotNull(result)
        assertEquals("true", result["status"].toString())
    }

    @Test
    fun set() = runTest {
        val result = leaderOS.credits.set("iZyy_", 666.666)

        assertNotNull(result)
        assertEquals("true", result["status"].toString())
    }
}