package fr.izy.leaderos.services

import fr.izy.leaderos.LeaderOSError
import fr.izy.leaderos.LeaderOSException
import fr.izy.leaderos.LeaderOSVersion
import fr.izy.leaderos.internal.GameSessionDto
import fr.izy.leaderos.internal.Service
import fr.izy.leaderos.internal.ServiceConfiguration
import fr.izy.leaderos.internal.TokenDto
import fr.izy.leaderos.models.GameSession
import fr.izy.leaderos.models.LoginResult
import fr.izy.leaderos.models.RegisterResult
import kotlinx.serialization.json.JsonElement
import java.time.LocalDateTime

class AuthService internal constructor(config: ServiceConfiguration) : Service(config) {

    /**
     * @param ip IP address of the user logging in.
     * @param userAgent User agent of the client the user logs in from.
     */
    suspend fun login(username: String, password: String, ip: String, userAgent: String): LoginResult {
        val body = mapOf(
            "username" to username,
            "password" to password,
            "ip" to ip,
            "useragent" to userAgent
        )
        return request("POST", "auth/login", form = body).data().decode()
    }

    /**
     * @param ip IP address of the user registering.
     * @param userAgent User agent of the client the user registers from.
     */
    suspend fun register(
        username: String,
        password: String,
        ip: String,
        userAgent: String,
        email: String? = null
    ): RegisterResult {
        val body = mapOf(
            "username" to username,
            "password" to password,
            "email" to email,
            "ip" to ip,
            "useragent" to userAgent
        )
        return request("POST", "auth/register", form = body).data().decode()
    }

    /**
     * Validates the two-factor code of a session returned by [login] when [LoginResult.isTfaRequired] is `true`.
     */
    suspend fun verifyTfa(token: String, code: String) {
        val body = mapOf(
            "token" to token,
            "code" to code
        )
        post<JsonElement>("auth/tfa/verify", body)
    }

    suspend fun isSessionValid(token: String): Boolean =
        try {
            get<JsonElement>("auth/sessions/$token")
            true
        } catch (e: LeaderOSException) {
            if (e.error == LeaderOSError.SESSION_NOT_FOUND) false else throw e
        }

    /**
     * Creates a session for a user and returns its token.
     *
     * @param expires `null` to let LeaderOS use its default session duration.
     */
    suspend fun createSession(userId: Int, ip: String, expires: LocalDateTime? = null): String {
        val body = mapOf(
            "userID" to userId,
            "ip" to ip,
            "expires" to expires
        )
        return request("POST", "auth/sessions", form = body).data().decode<TokenDto>().token
    }

    /**
     * Returns the in-game authentication state of a player (used by the official LeaderOS auth plugin).
     */
    suspend fun gameSession(username: String, ip: String, userAgent: String): GameSession {
        requireVersion(LeaderOSVersion.V6_0_0, "auth.gameSession")
        val body = mapOf(
            "username" to username,
            "ip" to ip,
            "useragent" to userAgent
        )
        val session = request("POST", "auth/game-sessions", form = body).data().decode<GameSessionDto>()
        return GameSession(session.state, session.token, session.user?.realname)
    }

    @Deprecated("Renamed", ReplaceWith("isSessionValid(token)"))
    suspend fun sessions(token: String): Boolean = isSessionValid(token)
}
