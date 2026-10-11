@file:UseSerializers(ApiBooleanSerializer::class)

package fr.izy.leaderos.models

import fr.izy.leaderos.internal.ApiBooleanSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
data class LoginResult(
    /**
     * Session token. When [isTfaRequired] is `true`, validate it with `auth.verifyTfa` first.
     */
    val token: String,
    val isTfaRequired: Boolean = false
)

@Serializable
data class RegisterResult(
    val token: String,
    val isEmailVerificationRequired: Boolean = false
)

/**
 * In-game authentication state of a player, see `auth.gameSession`.
 */
data class GameSession(
    val state: SessionState,

    /**
     * Session token, when LeaderOS created or found one.
     */
    val token: String?,

    /**
     * Username with its website casing, when the account exists.
     */
    val username: String?
) {
    val isAuthenticated: Boolean
        get() = state == SessionState.AUTHENTICATED
}

enum class SessionState {
    AUTHENTICATED,
    LOGIN_REQUIRED,
    REGISTER_REQUIRED,
    HAS_SESSION,
    TFA_REQUIRED,
    EMAIL_NOT_VERIFIED,
    USERNAME_CASE_MISMATCH,

    /**
     * State not known by this wrapper.
     */
    UNKNOWN
}
