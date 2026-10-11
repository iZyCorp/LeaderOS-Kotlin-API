import fr.izy.leaderos.LeaderOS
import io.github.cdimascio.dotenv.dotenv
import kotlinx.coroutines.test.runTest
import fr.izy.leaderos.models.DiscordUser
import kotlin.test.Test
import kotlin.test.assertEquals

class DiscordTest {

    val leaderOS: LeaderOS = LeaderOS(dotenv()["BASE_URL"], dotenv()["API_KEY"])

    @Test
    fun user() = runTest {
        val user: DiscordUser = leaderOS.discord.user("282184260671242249")

        assertEquals("282184260671242249", user.discordUserId)
    }
}
