import fr.izy.leaderos.LeaderOS
import fr.izy.leaderos.models.LoginResult
import fr.izy.leaderos.models.RegisterResult
import io.github.cdimascio.dotenv.dotenv
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertNotNull

class AuthTest {

    val leaderOS: LeaderOS = LeaderOS(dotenv()["BASE_URL"], dotenv()["API_KEY"])

    @Test
    fun login() = runTest {
        val result: LoginResult = leaderOS.auth.login("Sorry", "ButNo", "127.0.0.1", "LeaderOS-API-Wrapper")

        assertNotNull(result.token)
    }

    @Test
    fun register() = runTest {
        val result: RegisterResult = leaderOS.auth.register("Where", "IDontKnow", "127.0.0.1", "LeaderOS-API-Wrapper", "whoasked@gmail.com")

        assertNotNull(result.token)
    }

    @Test
    fun sessions() = runTest {
        val valid: Boolean = leaderOS.auth.isSessionValid("nop")

        assertFalse(valid)
    }
}
