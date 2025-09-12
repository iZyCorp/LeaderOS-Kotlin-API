package fr.izy.leaderos.internal

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

/**
 * Internal class used to make HTTP requests. Must be used only for service class.
 */
abstract class Service internal constructor(val config: ServiceConfiguration) {

    /**
     * A generic function to execute an HTTP request with a specified method, endpoint, and an optional body.
     *
     * This function builds the URL, sets the required headers, encodes the request body as form-data (if provided),
     * and executes the HTTP call. It then deserializes the JSON response into an object of type [T].
     *
     * @param method The HTTP method (e.g., "GET", "POST", "PUT", "PATCH").
     * @param endpoint The relative endpoint to which the request will be sent.
     * @param body An optional Map containing key-value pairs to be sent as form-data.
     * @return The deserialized response object of type [T].
     */
    private suspend inline fun <reified T> request(
        method: String,
        endpoint: String,
        body: Map<String, String>? = null
    ): T? {
        val url = "${config.baseUrl}/api/$endpoint"

        val builder = Request.Builder()
            .url(url)
            .addHeader("X-Api-Key", config.getAuthHeader())
            .addHeader("Content-Type", "application/x-www-form-urlencoded")

        if (body != null) {
            val formData = encodeAsFormData(body)
                ?: throw IllegalArgumentException("Request body may be in a wrong format.")
            val requestBody = formData.toRequestBody("application/x-www-form-urlencoded".toMediaType())
            builder.method(method, requestBody)
        } else {
            builder.method(method, null)
        }

        // Build the final request.
        val request = builder.build()

        // Execute the request and deserialize the JSON response into an object of type [T].
        return executeRequest(request)
    }

    /**
     * Executes a GET request on the specified endpoint.
     *
     * This method sends an HTTP GET request to the endpoint specified and expects a JSON response
     * which is deserialized into an object of type [T].
     *
     * @param endpoint The relative endpoint for the GET request.
     * @return The deserialized object of type [T] from the JSON response.
     */
    internal suspend inline fun <reified T> get(endpoint: String): T? =
        request("GET", endpoint)

    /**
     * Executes a POST request on the specified endpoint with the provided body.
     *
     * This method sends an HTTP POST request to the endpoint specified, encoding the provided
     * body as form-data, and expects a JSON response which is deserialized into an object of type [T].
     *
     * @param endpoint The relative endpoint for the POST request.
     * @param body A Map containing the request parameters to be encoded as form-data.
     * @return The deserialized object of type [T] from the JSON response.
     */
    internal suspend inline fun <reified T> post(endpoint: String, body: Map<String, String>): T? =
        request("POST", endpoint, body)

    /**
     * Executes a PUT request on the specified endpoint with the provided body.
     *
     * This method sends an HTTP PUT request to the endpoint specified, encoding the provided
     * body as form-data, and expects a JSON response which is deserialized into an object of type [T].
     *
     * @param endpoint The relative endpoint for the PUT request.
     * @param body A Map containing the request parameters to be encoded as form-data.
     * @return The deserialized object of type [T] from the JSON response.
     */
    internal suspend inline fun <reified T> put(endpoint: String, body: Map<String, String>): T? =
        request("PUT", endpoint, body)

    /**
     * Executes a PATCH request on the specified endpoint with the provided body.
     *
     * This method sends an HTTP PATCH request to the endpoint specified, encoding the provided
     * body as form-data, and expects a JSON response which is deserialized into an object of type [T].
     *
     * @param endpoint The relative endpoint for the PATCH request.
     * @param body A Map containing the request parameters to be encoded as form-data.
     * @return The deserialized object of type [T] from the JSON response.
     */
    internal suspend inline fun <reified T> patch(endpoint: String, body: Map<String, String>): T? =
        request("PATCH", endpoint, body)



    /**
     * Executes the HTTP request and deserializes the response into an object of type [T].
     * This method uses the IO dispatcher for network calls.
     */
    suspend inline fun <reified T> executeRequest(request: Request): T? {
        return withContext(Dispatchers.IO) {
            config.client.newCall(request).execute().use { response ->

                if (!response.isSuccessful) {
                    throw IOException("HTTP error ${response.code}")
                }
                val responseBody = response.body?.string() ?: throw IOException("Empty response body")

                try {
                    JSON.decodeFromString<T>(responseBody)
                } catch (e: Exception) {
                    println(e.message)
                    null
                }
            }
        }
    }

    fun encodeAsFormData(data: Map<String, String>?): String? =
        data?.entries?.joinToString("&") { "${it.key}=${it.value}" }


    companion object {
        val JSON = Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
        }
    }
}