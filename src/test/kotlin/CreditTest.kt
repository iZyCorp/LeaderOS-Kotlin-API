import fr.izy.leaderos.LeaderOS
import io.github.cdimascio.dotenv.dotenv
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class CreditTest {

    val leaderOS: LeaderOS = LeaderOS(dotenv()["BASE_URL"], dotenv()["API_KEY"])

    @Test
    fun show() = runTest {
        val result = leaderOS.credits.show("Dremy_115")

        assertEquals("EUR", result.currency)
    }

    @Test
    fun add() = runTest {
        val result = leaderOS.credits.add("iZyy_", 10.0)

        assertEquals("EUR", result.currency)
    }

    @Test
    fun remove() = runTest {
        val result = leaderOS.credits.remove("iZyy_", 10.0)

        assertEquals("EUR", result.currency)
    }

    @Test
    fun send() = runTest {
        val result = leaderOS.credits.send("Dremy_115", "iZyy_", 10.0)

        assertEquals("EUR", result.target.currency)
    }

    @Test
    fun set() = runTest {
        val result = leaderOS.credits.set("iZyy_", 666.666)

        assertEquals("EUR", result.currency)
    }
}
