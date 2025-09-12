import fr.izy.leaderos.LeaderOS
import io.github.cdimascio.dotenv.dotenv
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertNotNull

class VoteTest {

    val leaderOS: LeaderOS = LeaderOS(dotenv()["BASE_URL"], dotenv()["API_KEY"])

    @Test
    fun links() = runTest {
        val resultUsername = leaderOS.vote.links("iZyy_")
        val resultNoUsername = leaderOS.vote.links()

        assertNotNull(resultUsername)
        assertNotNull(resultNoUsername)
    }
}