package fr.izy.leaderos

import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import java.net.URLDecoder
import java.util.concurrent.TimeUnit

/**
 * LeaderOS instance backed by a local [MockWebServer]: no request leaves the machine.
 */
class MockLeaderOS(version: LeaderOSVersion = LeaderOSVersion.LATEST, basePath: String = "/") : AutoCloseable {

    val server = MockWebServer().apply { start() }

    val api = LeaderOS(server.url(basePath).toString(), "test-key", version = version)

    fun enqueue(body: String, code: Int = 200) {
        server.enqueue(MockResponse().setResponseCode(code).setBody(body))
    }

    fun takeRequest(): RecordedRequest =
        server.takeRequest(1, TimeUnit.SECONDS) ?: error("No request received")

    override fun close() = server.shutdown()
}

fun RecordedRequest.form(): Map<String, String> =
    body.readUtf8().split('&').filter { it.isNotEmpty() }.associate { pair ->
        val (name, value) = pair.split('=', limit = 2).let { it[0] to it.getOrElse(1) { "" } }
        URLDecoder.decode(name, "UTF-8") to URLDecoder.decode(value, "UTF-8")
    }

inline fun mockLeaderOS(version: LeaderOSVersion = LeaderOSVersion.LATEST, block: MockLeaderOS.() -> Unit) =
    MockLeaderOS(version).use(block)
