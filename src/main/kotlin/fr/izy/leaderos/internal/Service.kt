package fr.izy.leaderos.internal

import fr.izy.leaderos.LeaderOSError
import fr.izy.leaderos.LeaderOSException
import fr.izy.leaderos.LeaderOSVersion
import fr.izy.leaderos.UnsupportedVersionException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.serializer
import okhttp3.FormBody
import okhttp3.Request
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * Internal class used to make HTTP requests. Must be used only for service class.
 */
abstract class Service internal constructor(internal val config: ServiceConfiguration) {

    /**
     * `true` when the configured LeaderOS instance predates the 6.0 API rework.
     */
    internal val isLegacy: Boolean
        get() = config.version < LeaderOSVersion.V6_0_0

    /**
     * Fails fast, without sending any request, when [feature] does not exist in the configured version.
     */
    internal fun requireVersion(minimum: LeaderOSVersion, feature: String) {
        if (config.version < minimum) throw UnsupportedVersionException(feature, minimum, config.version)
    }

    internal suspend inline fun <reified T> get(path: String, query: Map<String, Any?> = emptyMap()): T =
        request("GET", path, query).decode()

    internal suspend inline fun <reified T> post(path: String, form: Map<String, Any?> = emptyMap()): T =
        request("POST", path, form = form).decode()

    internal suspend inline fun <reified T> patch(path: String, form: Map<String, Any?>): T =
        request("PATCH", path, form = form).decode()

    internal suspend inline fun <reified T> delete(path: String): T =
        request("DELETE", path).decode()

    internal inline fun <reified T> ApiResponse.decode(): T = decode(serializer<T>())

    /**
     * Sends a request to `<baseUrl>/api/<path>` and returns the parsed JSON body.
     *
     * Query parameters and form fields with a `null` value are skipped, every other value is URL-encoded.
     *
     * @throws LeaderOSException when LeaderOS answers with an error, or with something that is not JSON.
     */
    internal suspend fun request(
        method: String,
        path: String,
        query: Map<String, Any?> = emptyMap(),
        form: Map<String, Any?>? = null
    ): ApiResponse {
        val url = config.baseUrl.newBuilder()
            .addPathSegment("api")
            .addPathSegments(path)
            .apply { query.forEach { (name, value) -> if (value != null) addQueryParameter(name, value.toApiValue()) } }
            .build()

        val body = form?.let { fields ->
            FormBody.Builder()
                .apply { fields.forEach { (name, value) -> if (value != null) add(name, value.toApiValue()) } }
                .build()
        }

        val request = Request.Builder()
            .url(url)
            .header("X-Api-Key", config.getAuthHeader())
            .header("Accept", "application/json")
            .method(method, body)
            .build()

        return withContext(Dispatchers.IO) {
            config.client.newCall(request).execute().use { response ->
                val raw = response.body?.string().orEmpty()
                val json = runCatching { JSON.parseToJsonElement(raw) }.getOrNull()

                if (!response.isSuccessful || json.isErrorPayload()) {
                    throw apiError(response.code, method, path, json, raw)
                }
                if (json == null) {
                    throw LeaderOSException(
                        response.code,
                        LeaderOSError.UNEXPECTED_RESPONSE,
                        null,
                        "Unexpected response from $method /api/$path: not JSON (${raw.take(200)})"
                    )
                }
                ApiResponse(method, path, response.code, json)
            }
        }
    }

    internal companion object {
        val JSON = Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
            isLenient = true
        }
    }
}

/**
 * Error codes look like `USER_NOT_FOUND`, any other `error` value is a plain message.
 */
private val ERROR_CODE = Regex("[A-Z0-9_]+")

/**
 * LeaderOS sometimes answers `200 OK` with an error: `{"status": false, "error": ...}`,
 * or `{"error": "Route not found…", "code": 404}` without any status flag.
 */
private fun JsonElement?.isErrorPayload(): Boolean {
    if (this !is JsonObject) return false
    val status = (this["status"] as? JsonPrimitive)?.booleanOrNull
    val success = (this["success"] as? JsonPrimitive)?.booleanOrNull
    if (status == false || success == false) return true

    val error = this["error"] as? JsonPrimitive
    return status == null && success == null && error?.isString == true && "data" !in this
}

private fun Service.apiError(responseStatus: Int, method: String, path: String, json: JsonElement?, raw: String): LeaderOSException {
    val payload = json as? JsonObject
    val error = (payload?.get("error") as? JsonPrimitive)?.contentOrNull
    val code = error?.takeIf { ERROR_CODE.matches(it) }
    val apiMessage = (payload?.get("message") as? JsonPrimitive)?.contentOrNull ?: error?.takeIf { code == null }
    val httpStatus = (payload?.get("code") as? JsonPrimitive)?.intOrNull ?: responseStatus

    val parsed = when {
        code != null -> LeaderOSError.fromCode(code)
        error?.startsWith("Route not found", ignoreCase = true) == true -> LeaderOSError.ROUTE_NOT_FOUND
        else -> LeaderOSError.UNKNOWN
    }

    val detail = apiMessage ?: raw.take(200).ifBlank { "no response body" }
    val codePart = code?.let { ", $it" }.orEmpty()
    val hint = if (parsed == LeaderOSError.ROUTE_NOT_FOUND) " (configured LeaderOS version: ${config.version})" else ""

    return LeaderOSException(
        httpStatus,
        parsed,
        code,
        "$method /api/$path failed (HTTP $httpStatus$codePart): $detail$hint"
    )
}

private fun Any.toApiValue(): String = when (this) {
    is BigDecimal -> toPlainString()
    is Double, is Float -> BigDecimal.valueOf((this as Number).toDouble()).toPlainString()
    is LocalDateTime -> format(API_DATE_TIME)
    else -> toString()
}
