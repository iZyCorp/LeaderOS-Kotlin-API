package fr.izy.leaderos

import fr.izy.leaderos.models.BazaarItemInput
import fr.izy.leaderos.models.BuyItem
import fr.izy.leaderos.models.DonationType
import kotlinx.coroutines.test.runTest
import java.math.BigDecimal
import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse

/**
 * Checks the exact request sent for each LeaderOS version.
 */
class RoutingTest {

    private val v5 = LeaderOSVersion.V5_5_9
    private val balance = """{"current_credits":"20.00 EUR","current_raw_credits":20.5,"currency":"EUR"}"""

    @Test
    fun `credits add uses the new route on 6x`() = runTest {
        mockLeaderOS {
            enqueue(balance)

            val result = api.credits.add("iZyy_", 10.0)

            val request = takeRequest()
            assertEquals("POST", request.method)
            assertEquals("/api/credits/add", request.path)
            assertEquals("test-key", request.getHeader("X-Api-Key"))
            assertEquals(mapOf("target_username" to "iZyy_", "amount" to "10.0"), request.form())
            assertEquals(BigDecimal("20.5"), result.amount)
            assertEquals("20.00 EUR", result.formatted)
            assertEquals("EUR", result.currency)
        }
    }

    @Test
    fun `credits add uses the legacy route on 5x and fetches the balance when missing`() = runTest {
        mockLeaderOS(v5) {
            enqueue("""{"status":true}""")
            enqueue("""{"credits":"30.00 EUR","raw_credits":"30.00","currency":"EUR"}""")

            val result = api.credits.add("iZyy_", BigDecimal("10"))

            val add = takeRequest()
            assertEquals("/api/credits/iZyy_/add", add.path)
            assertEquals(mapOf("amount" to "10"), add.form())
            assertEquals("/api/credits/iZyy_", takeRequest().path)
            assertEquals(BigDecimal("30.00"), result.amount)
        }
    }

    @Test
    fun `credits show and set`() = runTest {
        mockLeaderOS {
            enqueue("""{"credits":"10.25 USD","raw_credits":10.25,"currency":"USD"}""")
            enqueue(balance)

            assertEquals(BigDecimal("10.25"), api.credits.show("iZyy_").amount)
            api.credits.set("iZyy_", 666)

            assertEquals("/api/credits?username=iZyy_", takeRequest().path)
            val set = takeRequest()
            assertEquals("/api/credits/set", set.path)
            assertEquals(mapOf("target_username" to "iZyy_", "amount" to "666"), set.form())
        }
    }

    @Test
    fun `credits send on both versions`() = runTest {
        val transfer = """{"current_credits":"8.25 USD","current_raw_credits":8.25,
            "target_current_credits":"2.25 USD","target_current_raw_credits":2.25,"currency":"USD"}"""

        mockLeaderOS {
            enqueue(transfer)
            val result = api.credits.send("A", "B", 2)
            val request = takeRequest()
            assertEquals("/api/credits/send", request.path)
            assertEquals(mapOf("sender_username" to "A", "target_username" to "B", "amount" to "2"), request.form())
            assertEquals(BigDecimal("8.25"), result.sender.amount)
            assertEquals(BigDecimal("2.25"), result.target.amount)
        }

        mockLeaderOS(v5) {
            enqueue(transfer)
            api.credits.send("A", "B", 2)
            val request = takeRequest()
            assertEquals("/api/credits/A/send", request.path)
            assertEquals(mapOf("target" to "B", "target_username" to "B", "amount" to "2"), request.form())
        }
    }

    @Test
    fun `user profile on both versions`() = runTest {
        val user = """{"id":"2","username":"izyy_","realname":"iZyy_"}"""

        mockLeaderOS {
            enqueue(user)
            enqueue(user)
            api.user.profile("iZyy_")
            api.user.profile(3)
            assertEquals("/api/users?username=iZyy_", takeRequest().path)
            assertEquals("/api/users?userID=3", takeRequest().path)
        }

        mockLeaderOS(v5) {
            enqueue(user)
            api.user.profile("iZyy_")
            assertEquals("/api/users/iZyy_", takeRequest().path)
        }
    }

    @Test
    fun `store listing on both versions`() = runTest {
        mockLeaderOS {
            enqueue("""{"categories":[]}""")
            enqueue("""{"categories":[]}""")
            api.store.listing()
            api.store.listing("iZyy_")
            assertEquals("/api/store/listing", takeRequest().path)
            assertEquals("/api/store/listing?username=iZyy_", takeRequest().path)
        }

        mockLeaderOS(v5) {
            enqueue("""{"categories":[]}""")
            api.store.listing("iZyy_")
            assertEquals("/api/store/listing?user=iZyy_", takeRequest().path)
        }
    }

    @Test
    fun `store buy encodes products as a PHP array`() = runTest {
        val purchase = """{"status":true,"data":{"orderID":"123","credits":"10.00","products":[{"id":1,"quantity":2,"variables":[]}],
            "cashback":"1.00","total":"10.00","subtotal":"10.00","discount":"0.00"}}"""

        mockLeaderOS {
            enqueue(purchase)

            val result = api.store.buy(3, BuyItem(1, quantity = 2), BuyItem(4))

            val request = takeRequest()
            assertEquals("/api/store/buy", request.path)
            assertEquals(
                mapOf(
                    "userID" to "3",
                    "products[0][id]" to "1",
                    "products[0][quantity]" to "2",
                    "products[1][id]" to "4",
                    "products[1][quantity]" to "1"
                ),
                request.form()
            )
            assertEquals(123, result.orderId)
            assertEquals(BigDecimal("10.00"), result.credits)
            assertEquals(2, result.products.single().quantity)
        }

        mockLeaderOS(v5) {
            enqueue(purchase)
            api.store.buy(3, BuyItem(1))
            assertEquals("3", takeRequest().form()["user"])
        }
    }

    @Test
    fun `store donations`() = runTest {
        mockLeaderOS {
            enqueue("[]")
            enqueue("[]")
            api.store.donations(DonationType.TOP_MONTHLY, limit = 10)
            api.store.donations(DonationType.LATEST)
            assertEquals("/api/store/donations?type=top-monthly&limit=10", takeRequest().path)
            assertEquals("/api/store/donations?type=latest", takeRequest().path)
        }
    }

    @Test
    fun `user roles management`() = runTest {
        mockLeaderOS {
            repeat(3) { enqueue("[]") }

            api.user.addRole(3, 5, LocalDateTime.of(2026, 11, 10, 12, 0))
            api.user.updateRole(3, 5)
            api.user.removeRole(3, 5)

            val add = takeRequest()
            assertEquals("POST", add.method)
            assertEquals("/api/users/3/roles", add.path)
            assertEquals(mapOf("roleID" to "5", "expiryDate" to "2026-11-10 12:00:00"), add.form())

            val update = takeRequest()
            assertEquals("PATCH", update.method)
            assertEquals("/api/users/3/roles/5", update.path)
            assertEquals(mapOf("roleID" to "5"), update.form())

            val remove = takeRequest()
            assertEquals("DELETE", remove.method)
            assertEquals("/api/users/3/roles/5", remove.path)
        }
    }

    @Test
    fun `bazaar sends the owner only on 5x`() = runTest {
        val item = BazaarItemInput(serverId = 1, itemId = "DIAMOND_SWORD", amount = 1, durability = 0, maxDurability = 1561, base64 = "rO0+/=")

        mockLeaderOS {
            enqueue("""{"success":true}""")
            enqueue("[]")
            api.bazaar.addItem(3, item)
            api.bazaar.items(3, serverId = 1)

            val add = takeRequest()
            assertEquals("/api/bazaar/storages/3/items", add.path)
            assertEquals(
                mapOf("serverID" to "1", "itemID" to "DIAMOND_SWORD", "amount" to "1", "durability" to "0", "maxDurability" to "1561", "base64" to "rO0+/="),
                add.form()
            )
            assertEquals("/api/bazaar/storages/3/items?serverID=1", takeRequest().path)
        }

        mockLeaderOS(v5) {
            enqueue("""{"success":true}""")
            api.bazaar.addItem(3, item)
            assertEquals("3", takeRequest().form()["owner"])
        }
    }

    @Test
    fun `support messages are URL-encoded`() = runTest {
        mockLeaderOS {
            enqueue("""{"success":true,"data":{"id":"9"}}""")

            val id = api.support.reply(6, "Prix: 5€ & -50% + #promo?", accountId = 1, isAdmin = true)

            val request = takeRequest()
            assertEquals("/api/support/tickets/6/messages", request.path)
            assertEquals(
                mapOf("message" to "Prix: 5€ & -50% + #promo?", "accountID" to "1", "userID" to "1", "isAdmin" to "true"),
                request.form()
            )
            assertEquals(9, id)
        }
    }

    @Test
    fun `support ticket lifecycle`() = runTest {
        mockLeaderOS {
            enqueue("""{"success":true,"data":{"id":"6"}}""")
            enqueue("""{"success":true,"data":{"id":"6"}}""")

            val id = api.support.createTicket(accountId = 3, categoryId = 1, title = "Bug", message = "Hello")
            api.support.closeTicket(id)

            val create = takeRequest()
            assertEquals("/api/support/tickets", create.path)
            assertEquals(mapOf("accountID" to "3", "categoryID" to "1", "title" to "Bug", "message" to "Hello", "statusID" to "1"), create.form())
            val close = takeRequest()
            assertEquals("POST", close.method)
            assertEquals("/api/support/tickets/6/close", close.path)
        }
    }

    @Test
    fun `query values are URL-encoded and null values skipped`() = runTest {
        mockLeaderOS {
            enqueue("[]")
            enqueue("[]")
            api.vote.links("a b&c")
            api.vote.links()
            assertEquals("/api/vote-links?username=a%20b%26c", takeRequest().path)
            assertEquals("/api/vote-links", takeRequest().path)
        }
    }

    @Test
    fun `base url with a sub path`() = runTest {
        MockLeaderOS(basePath = "/shop").use {
            it.enqueue("[]")
            it.api.vote.links()
            assertEquals("/shop/api/vote-links", it.takeRequest().path)
        }
    }

    @Test
    fun `6x only endpoints fail before any request on 5x`() = runTest {
        mockLeaderOS(v5) {
            val error = assertFailsWith<UnsupportedVersionException> { api.store.communityGoal() }
            assertEquals("store.communityGoal requires LeaderOS >= 6.0.0 (configured: 5.5.9)", error.message)
            assertFailsWith<UnsupportedVersionException> { api.auth.gameSession("iZyy_", "1.1.1.1", "ua") }
            assertFailsWith<UnsupportedVersionException> { api.integrations.aiCommand("hi", "en_us") }
            assertFailsWith<UnsupportedVersionException> { api.integrations.verifyInGame("123", "iZyy_", "uuid") }
            assertEquals(0, server.requestCount)
        }
    }

    @Test
    fun `undocumented endpoints routes`() = runTest {
        mockLeaderOS {
            enqueue("""{"status":true,"data":{"state":"AUTHENTICATED","token":"abc","user":{"realname":"iZyy_"}}}""")
            enqueue("""{"status":true}""")
            enqueue("""{"status":true,"data":{"message":"Hello!"}}""")

            val session = api.auth.gameSession("iZyy_", "1.1.1.1", "MyLauncher")
            api.integrations.verifyInGame("123456", "iZyy_", "uuid-1")
            val answer = api.integrations.aiCommand("hi", "fr_fr")

            val sessionRequest = takeRequest()
            assertEquals("/api/auth/game-sessions", sessionRequest.path)
            assertEquals(mapOf("username" to "iZyy_", "ip" to "1.1.1.1", "useragent" to "MyLauncher"), sessionRequest.form())
            assertEquals("iZyy_", session.username)
            assertEquals(true, session.isAuthenticated)

            val verify = takeRequest()
            assertEquals("/api/integrations/leaderos-id/verify-ingame", verify.path)
            assertEquals(mapOf("code" to "123456", "username" to "uuid-1", "realname" to "iZyy_"), verify.form())

            assertEquals("/api/integrations/leaderos-ai/ai-command/ingame", takeRequest().path)
            assertEquals("Hello!", answer)
        }
    }

    @Test
    fun `version parsing and ordering`() {
        assertEquals(LeaderOSVersion(6, 3, 3), LeaderOSVersion.parse("6.3.3"))
        assertEquals(LeaderOSVersion(6, 0, 0), LeaderOSVersion.parse("v6"))
        assertFalse(LeaderOSVersion.V5_5_9 >= LeaderOSVersion.V6_0_0)
        assertEquals(LeaderOSVersion.V6_3_3, LeaderOSVersion.LATEST)
    }
}
