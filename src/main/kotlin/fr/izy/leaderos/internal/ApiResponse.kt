package fr.izy.leaderos.internal

import fr.izy.leaderos.LeaderOSError
import fr.izy.leaderos.LeaderOSException
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

/**
 * Successful LeaderOS response, kept as a JSON tree until the service decodes it.
 */
internal class ApiResponse(
    val method: String,
    val path: String,
    val httpStatus: Int,
    val body: JsonElement
) {

    /**
     * Some endpoints wrap their payload in `{"status": true, "data": {...}}`.
     * Returns the inner payload when present, the whole body otherwise.
     */
    fun data(): ApiResponse =
        (body as? JsonObject)?.get("data")?.let { ApiResponse(method, path, httpStatus, it) } ?: this

    /**
     * Some single-item endpoints answer with a one-element array.
     */
    fun firstIfArray(): ApiResponse {
        val array = body as? JsonArray ?: return this
        val first = array.firstOrNull() ?: throw unexpected("empty array")
        return ApiResponse(method, path, httpStatus, first)
    }

    fun <T> decode(deserializer: DeserializationStrategy<T>): T =
        try {
            Service.JSON.decodeFromJsonElement(deserializer, body)
        } catch (e: IllegalArgumentException) {
            // SerializationException extends IllegalArgumentException
            throw unexpected(e.message, e)
        }

    private fun unexpected(reason: String?, cause: Throwable? = null) = LeaderOSException(
        httpStatus,
        LeaderOSError.UNEXPECTED_RESPONSE,
        null,
        "Unexpected response from $method /api/$path: $reason",
        cause
    )
}
