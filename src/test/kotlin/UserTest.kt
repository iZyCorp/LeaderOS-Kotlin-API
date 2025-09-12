import fr.izy.leaderos.LeaderOS
import io.github.cdimascio.dotenv.dotenv
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertNotNull

class UserTest {

    val leaderOS: LeaderOS = LeaderOS(dotenv()["BASE_URL"], dotenv()["API_KEY"])

    @Test
    fun profile() = runTest {
        val resultUsername = leaderOS.user.profile("iZyy_")
        val resultId = leaderOS.user.profile(3)

        assertNotNull(resultUsername)
        assertNotNull(resultId)
    }

    @Test
    fun roles() = runTest {
        val resultUsername = leaderOS.user.roles("iZyy_")
        val resultId = leaderOS.user.roles(3)

        assertNotNull(resultUsername)
        assertNotNull(resultId)
    }

    @Test
    fun tickets() = runTest {
        val resultUsername = leaderOS.user.tickets("iZyy_")
        val resultId = leaderOS.user.tickets(3)

        assertNotNull(resultUsername)
        assertNotNull(resultId)
    }

    @Test
    fun orders() = runTest {
        val resultUsername = leaderOS.user.orders("iZyy_")
        val resultId = leaderOS.user.orders(3)

        assertNotNull(resultUsername)
        assertNotNull(resultId)
    }
}