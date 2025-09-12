import fr.izy.leaderos.LeaderOS
import io.github.cdimascio.dotenv.dotenv
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertNotNull

class AuthTest {

    val leaderOS: LeaderOS = LeaderOS(dotenv()["BASE_URL"], dotenv()["API_KEY"])

    @Test
    fun login() = runTest {
        val token: String? = leaderOS.auth.login("Sorry", "ButNo")

        assertNotNull(token)
    }

    @Test
    fun register() = runTest {
        val token: String? = leaderOS.auth.register("Where", "IDontKnow", "whoasked@gmail.com", "nop")

        assertNotNull(token)
    }

    @Test
    fun sessions() = runTest {
        val token: Boolean? = leaderOS.auth.sessions("nop")

        assertNotNull(token)
    }
}