package fr.izy.leaderos

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ErrorHandlingTest {

    @Test
    fun `API errors are thrown with their code`() = runTest {
        mockLeaderOS {
            enqueue("""{"status":false,"error":"USER_NOT_FOUND","message":"User not found!"}""", code = 404)

            val error = assertFailsWith<LeaderOSException> { api.user.profile("nobody") }
            assertEquals(404, error.httpStatus)
            assertEquals(LeaderOSError.USER_NOT_FOUND, error.error)
            assertEquals("GET /api/users failed (HTTP 404, USER_NOT_FOUND): User not found!", error.message)
        }
    }

    @Test
    fun `errors sent with a 200 status are thrown too`() = runTest {
        mockLeaderOS {
            enqueue("""{"status":false,"error":"NOT_ENOUGH_CREDITS","message":"Not enough credits"}""")

            val error = assertFailsWith<LeaderOSException> { api.credits.remove("iZyy_", 50) }
            assertEquals(LeaderOSError.NOT_ENOUGH_CREDITS, error.error)
        }
    }

    @Test
    fun `unlinked discord account`() = runTest {
        mockLeaderOS {
            enqueue("""{"status":false,"error":"USER_NOT_FOUND","message":"User not found!"}""", code = 404)

            val error = assertFailsWith<LeaderOSException> { api.discord.user("282184260671242249") }
            assertEquals(404, error.httpStatus)
            assertEquals(LeaderOSError.USER_NOT_FOUND, error.error)
            assertEquals("/api/integrations/discord/users/282184260671242249", takeRequest().path)
        }
    }

    @Test
    fun `missing routes answered with a 200 status`() = runTest {
        mockLeaderOS {
            enqueue("""{"error":"Route not found: /api/integrations/discord/users/1","code":404}""")

            val error = assertFailsWith<LeaderOSException> { api.discord.user("1") }
            assertEquals(404, error.httpStatus)
            assertEquals(LeaderOSError.ROUTE_NOT_FOUND, error.error)
            assertEquals(null, error.errorCode)
            assertEquals(
                "GET /api/integrations/discord/users/1 failed (HTTP 404): Route not found: /api/integrations/discord/users/1 (configured LeaderOS version: 6.3.3)",
                error.message
            )
        }
    }

    @Test
    fun `legacy route on a 6x website`() = runTest {
        // Real answer of a LeaderOS 6.3.3 website to a 5.x route
        mockLeaderOS(LeaderOSVersion.V5_5_9) {
            enqueue("""{"error":"Route not found: \"\/api\/credits\/zz_inexistant_zz\/\"","code":404}""")

            val error = assertFailsWith<LeaderOSException> { api.credits.show("zz_inexistant_zz") }
            assertEquals(404, error.httpStatus)
            assertEquals(LeaderOSError.ROUTE_NOT_FOUND, error.error)
            assertTrue("configured LeaderOS version: 5.5.9" in error.message.orEmpty())
        }
    }

    @Test
    fun `invalid API key`() = runTest {
        mockLeaderOS {
            enqueue("""{"status":false,"error":"INVALID_API_KEY","message":"Invalid API key!"}""", code = 401)

            val error = assertFailsWith<LeaderOSException> { api.discord.settings() }
            assertEquals(401, error.httpStatus)
            assertEquals(LeaderOSError.INVALID_API_KEY, error.error)
        }
    }

    @Test
    fun `error codes without status flag`() = runTest {
        mockLeaderOS {
            enqueue("""{"error":"USER_NOT_FOUND"}""")

            val error = assertFailsWith<LeaderOSException> { api.user.profile(3) }
            assertEquals(200, error.httpStatus)
            assertEquals(LeaderOSError.USER_NOT_FOUND, error.error)
        }
    }

    @Test
    fun `unknown error codes are kept raw`() = runTest {
        mockLeaderOS {
            enqueue("""{"status":false,"error":"SOMETHING_NEW"}""", code = 400)

            val error = assertFailsWith<LeaderOSException> { api.vote.links() }
            assertEquals(LeaderOSError.UNKNOWN, error.error)
            assertEquals("SOMETHING_NEW", error.errorCode)
        }
    }

    @Test
    fun `non JSON errors`() = runTest {
        mockLeaderOS {
            enqueue("<html>Internal Server Error</html>", code = 500)

            val error = assertFailsWith<LeaderOSException> { api.vote.links() }
            assertEquals(500, error.httpStatus)
            assertEquals(LeaderOSError.UNKNOWN, error.error)
            assertTrue("Internal Server Error" in error.message.orEmpty())
        }
    }

    @Test
    fun `unexpected payloads`() = runTest {
        mockLeaderOS {
            enqueue("""{"unexpected":true}""")

            val error = assertFailsWith<LeaderOSException> { api.user.profile(3) }
            assertEquals(LeaderOSError.UNEXPECTED_RESPONSE, error.error)
        }
    }

    @Test
    fun `missing session is not an error`() = runTest {
        mockLeaderOS {
            enqueue("""{"status":true}""")
            enqueue("""{"status":false,"error":"SESSION_NOT_FOUND","message":"Session not found!"}""", code = 404)

            assertTrue(api.auth.isSessionValid("valid"))
            assertFalse(api.auth.isSessionValid("expired"))
            assertEquals("/api/auth/sessions/valid", takeRequest().path)
        }
    }
}
