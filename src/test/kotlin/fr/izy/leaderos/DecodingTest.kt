package fr.izy.leaderos

import fr.izy.leaderos.models.TicketStatus
import kotlinx.coroutines.test.runTest
import java.math.BigDecimal
import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Decodes the response samples of https://developer.leaderos.net into typed models.
 */
class DecodingTest {

    @Test
    fun `user profile with linked accounts`() = runTest {
        mockLeaderOS {
            enqueue(
                """{"id":"2","username":"leaderos","email":"test@gmail.com","credit":"150.00","isVerified":"1","authStatus":"0",
                "creationIP":"1.1.1.1","creationDate":"2022-09-15 10:41:44","linkedAccounts":{"discord":"111111111111"}}"""
            )
            // PHP encodes an empty associative array as []
            enqueue("""{"id":"3","username":"other","credit":"0.00","linkedAccounts":[]}""")

            val user = api.user.profile("leaderos")
            assertEquals(2, user.id)
            assertEquals(BigDecimal("150.00"), user.credit)
            assertTrue(user.isVerified)
            assertEquals(LocalDateTime.of(2022, 9, 15, 10, 41, 44), user.creationDate)
            assertEquals(mapOf("discord" to "111111111111"), user.linkedAccounts)

            assertEquals(emptyMap(), api.user.profile("other").linkedAccounts)
        }
    }

    @Test
    fun `permanent and temporary roles`() = runTest {
        mockLeaderOS {
            enqueue(
                """[
                {"id":"2","discordRoleID":"673855784005795840","name":"Admin","slug":"admin","priority":"99","expiryDate":"1000-01-01 00:00:00"},
                {"id":"3","discordRoleID":null,"name":"VIP","slug":"vip","priority":"5","expiryDate":"2023-09-03 12:30:45"}
                ]"""
            )

            val (admin, vip) = api.user.roles(2)
            assertTrue(admin.isPermanent)
            assertEquals("673855784005795840", admin.discordRoleId)
            assertFalse(vip.isPermanent)
            assertEquals(LocalDateTime.of(2023, 9, 3, 12, 30, 45), vip.expiryDate)
            assertNull(vip.discordRoleId)
        }
    }

    @Test
    fun `donations use snake case keys`() = runTest {
        mockLeaderOS {
            enqueue("""[{"display_name":"LeaderOS","username":"leaderos","total":"5.00 GBP","raw_total":"5.00","currency":"GBP","date":"2023-09-13 23:35:12"}]""")

            val donation = api.store.donations(fr.izy.leaderos.models.DonationType.LATEST).single()
            assertEquals("LeaderOS", donation.displayName)
            assertEquals(BigDecimal("5.00"), donation.amount)
            assertEquals("5.00 GBP", donation.formatted)
        }
    }

    @Test
    fun `store listing with nested categories and 6x pricing`() = runTest {
        mockLeaderOS {
            enqueue(
                """{"categories":[{"id":"8","parentID":"0","name":"Factions","slug":"factions","imageID":"017d","imageType":"jpg","priority":"0",
                "minecraftStatus":"1","minecraftTitle":"&amp;aFactions","minecraftDescription":"","minecraftItem":"diamond_sword","isActive":"1",
                "subcategories":[{"id":"2","parentID":"8","name":"Money","slug":"money","priority":"0","minecraftStatus":"0","isActive":"1",
                  "subcategories":[],"products":[{"id":"8","categoryID":"2","giveRoleID":"0","name":"100M Money","price":"10.00","discountedPrice":"9.00",
                  "discountExpiryDate":"1000-01-01 00:00:00","duration":"0","stock":"10","priority":"0","isFeaturedProduct":"0","minecraftStatus":"0",
                  "minecraftItem":"default","isActive":"1","creationDate":"2022-09-15 19:08:35",
                  "total":9,"totalFormatted":"9.00 EUR","originalPrice":10,"discount":1,"discountPercent":10,"restricted":false}]}],
                "products":[]}]}"""
            )

            val factions = api.store.listing("iZyy_").single()
            assertTrue(factions.minecraftStatus)
            val product = factions.subcategories.single().products.single()
            assertEquals(BigDecimal("10.00"), product.price)
            assertNull(product.discountExpiryDate)
            assertEquals(10, product.stock)
            assertEquals(BigDecimal("9"), product.total)
            assertEquals(false, product.restricted)
        }
    }

    @Test
    fun `support ticket with category and messages`() = runTest {
        mockLeaderOS {
            enqueue(
                """{"id":"6","accountID":"1","categoryID":"1","title":"Help","statusID":"2","readStatus":"0",
                "updateDate":"2023-07-19 20:13:37","creationDate":"2023-07-19 20:13:37","category":{"id":"1","name":"Cheat"},
                "messages":[{"id":"6","accountID":"1","message":"Hello","writeLocation":"1","creationDate":"2023-07-19 20:13:37"}]}"""
            )
            enqueue("""[{"id":"1","name":"Cheat"}]""")

            val ticket = api.support.ticket(6)
            assertEquals(TicketStatus.ANSWERED, ticket.status)
            assertEquals("Cheat", ticket.category?.name)
            assertEquals("Hello", ticket.messages.single().message)

            // The docs show this endpoint answering with a one-element array
            assertEquals("Cheat", api.support.category(1).name)
        }
    }

    @Test
    fun `discord user and settings`() = runTest {
        mockLeaderOS {
            enqueue(
                """{"id":"1","username":"demo","realname":"demo","credit":"350.00","avatar":"https://minotar.net/helm/LeaderOS/128.png",
                "isVerified":"1","authStatus":"0","creationIP":"1.1.1.1","creationDate":"2022-09-15 09:54:08","discordUserID":"477886791769391124",
                "roles":[{"id":"1","name":"Admin","slug":"admin","discordRoleID":"127886791769391123","expiryDate":"1000-01-01 00:00:00"}]}"""
            )
            enqueue(
                """{"roleSyncingStatus":true,"syncedRoleID":"1131621997231743046","ticketStatus":true,
                "ticketCategoryID":"1148224808119701576","ticketStaffRoleIDs":["1148268311889575976"]}"""
            )

            val user = api.discord.user("477886791769391124")
            assertEquals("477886791769391124", user.discordUserId)
            assertTrue(user.roles.single().isPermanent)

            val settings = api.discord.settings()
            assertTrue(settings.roleSyncingStatus)
            assertEquals(listOf("1148268311889575976"), settings.ticketStaffRoleIds)
        }
    }

    @Test
    fun `unset discord settings are empty strings`() = runTest {
        // Real answer of a LeaderOS 6.3.3 website
        mockLeaderOS {
            enqueue(
                """{"guildID":"466315666383699989","roleSyncingStatus":true,"setNicknameStatus":false,"syncedRoleID":"",
                "ticketStatus":false,"ticketCategoryID":"","ticketStaffRoleIDs":[""]}"""
            )

            val settings = api.discord.settings()
            assertEquals("466315666383699989", settings.guildId)
            assertNull(settings.syncedRoleId)
            assertNull(settings.ticketCategoryId)
            assertEquals(emptyList(), settings.ticketStaffRoleIds)
        }
    }

    @Test
    fun `bazaar items`() = runTest {
        mockLeaderOS {
            enqueue(
                """[{"id":"4","owner":"1","serverID":"3","itemID":"ENCHANTING_TABLE","modelID":null,"name":"Enchanting Table","lore":null,
                "amount":"1","durability":"1","maxDurability":"0","enchantments":null,"base64":"rO0AB","description":null,"price":"0.00",
                "sold":"0","creationDate":"2022-08-24 00:15:03"}]"""
            )

            val item = api.bazaar.items(1).single()
            assertEquals(1, item.ownerId)
            assertEquals("ENCHANTING_TABLE", item.itemId)
            assertFalse(item.isSold)
        }
    }

    @Test
    fun `auth responses are unwrapped from data`() = runTest {
        mockLeaderOS {
            enqueue("""{"status":true,"data":{"token":"202cb962ac59075b964b07152d234b70","isTfaRequired":true}}""")
            enqueue("""{"status":true,"token":"abc"}""")

            val login = api.auth.login("iZyy_", "pwd", "1.1.1.1", "MyLauncher")
            assertEquals("202cb962ac59075b964b07152d234b70", login.token)
            assertTrue(login.isTfaRequired)

            assertEquals("abc", api.auth.createSession(3, "1.1.1.1"))
        }
    }

    @Test
    fun `community goal`() = runTest {
        mockLeaderOS {
            enqueue("""{"is_active":true,"currency":"EUR","total_raised":120.5,"goal_amount":500,"percentage_achieved":24.1}""")

            val goal = api.store.communityGoal()
            assertTrue(goal.isActive)
            assertEquals(BigDecimal("120.5"), goal.totalRaised)
            assertEquals(24.1, goal.percentageAchieved)
        }
    }
}
