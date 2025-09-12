import fr.izy.leaderos.LeaderOS
import io.github.cdimascio.dotenv.dotenv
import kotlinx.coroutines.test.runTest
import fr.izy.leaderos.models.Category
import fr.izy.leaderos.models.Donation
import fr.izy.leaderos.models.OrderStatus
import org.junit.jupiter.api.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class StoreTest {

    val leaderOS: LeaderOS = LeaderOS(dotenv()["BASE_URL"], dotenv()["API_KEY"])

    @Test
    fun listing() = runTest {
        val categories: List<Category>? = leaderOS.store.listing()

        assertNotNull(categories)
    }

    @Test
    fun products() = runTest {
        val products = leaderOS.store.products()

        assertNotNull(products)
        assertTrue(products.isNotEmpty())
    }

    @Test
    fun product() = runTest {
        val products = leaderOS.store.product(1)

        assertNotNull(products)
    }


    @Test
    fun categories() = runTest {
        val categories: List<Category>? = leaderOS.store.categories()
        val category: Category? = leaderOS.store.category(1)

        assertNotNull(categories)
        assertNotNull(category)
    }

    @Test
    fun buy() = runTest {
        val orderStatus: OrderStatus? = leaderOS.store.buy(arrayOf(1, 2, 3, 4, 5), 1)

        assertNotNull(orderStatus)
    }

    @Test
    fun donations() = runTest {
        val donations: List<Donation>? = leaderOS.store.donations("latest", 10)

        assertNotNull(donations)
    }
}