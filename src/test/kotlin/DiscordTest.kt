import fr.izy.leaderos.LeaderOS
import io.github.cdimascio.dotenv.dotenv
import kotlinx.coroutines.test.runTest
import fr.izy.leaderos.models.User
import kotlin.test.Test
import kotlin.test.assertNotNull

class DiscordTest {

    val leaderOS: LeaderOS = LeaderOS(dotenv()["BASE_URL"], dotenv()["API_KEY"])

    @Test
    fun getAccountByDiscordId() = runTest {
        val user: User? = leaderOS.discord.getAccountByDiscordId("Who")

        assertNotNull(user)
    }
}