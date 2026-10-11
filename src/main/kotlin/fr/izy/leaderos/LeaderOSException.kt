package fr.izy.leaderos

/**
 * Thrown when LeaderOS answers with an error (`{"status": false, "error": "USER_NOT_FOUND", ...}`)
 * or with a payload the wrapper cannot read ([LeaderOSError.UNEXPECTED_RESPONSE]).
 *
 * Network failures are still reported as [java.io.IOException].
 */
class LeaderOSException internal constructor(
    /**
     * HTTP status code returned by LeaderOS.
     */
    val httpStatus: Int,

    /**
     * Parsed error, [LeaderOSError.UNKNOWN] when LeaderOS sent a code this wrapper does not know.
     */
    val error: LeaderOSError,

    /**
     * Raw error code as sent by LeaderOS, e.g. `USER_NOT_FOUND`.
     */
    val errorCode: String?,

    message: String,
    cause: Throwable? = null
) : RuntimeException(message, cause)

/**
 * Thrown before any request is sent when an endpoint does not exist in the configured [LeaderOSVersion].
 */
class UnsupportedVersionException internal constructor(
    val feature: String,
    val requiredVersion: LeaderOSVersion,
    val configuredVersion: LeaderOSVersion
) : UnsupportedOperationException("$feature requires LeaderOS >= $requiredVersion (configured: $configuredVersion)")
