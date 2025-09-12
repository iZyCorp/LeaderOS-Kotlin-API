package fr.izy.leaderos.services

import fr.izy.leaderos.LeaderOSVersion
import fr.izy.leaderos.internal.MessageDto
import fr.izy.leaderos.internal.Service
import fr.izy.leaderos.internal.ServiceConfiguration
import kotlinx.serialization.json.JsonElement

/**
 * LeaderOS integrations used by the official Minecraft plugin. These endpoints are not in the public docs.
 */
class IntegrationService internal constructor(config: ServiceConfiguration) : Service(config) {

    /**
     * Links a Minecraft account to LeaderOS ID with the code shown on the website.
     *
     * @param username Minecraft username.
     * @param uuid Minecraft UUID.
     */
    suspend fun verifyInGame(code: String, username: String, uuid: String) {
        requireVersion(LeaderOSVersion.V6_0_0, "integrations.verifyInGame")
        // Matches the official plugin: `username` holds the UUID and `realname` the Minecraft name
        val body = mapOf(
            "code" to code,
            "username" to uuid,
            "realname" to username
        )
        post<JsonElement>("integrations/leaderos-id/verify-ingame", body)
    }

    /**
     * Asks LeaderOS AI and returns its answer.
     *
     * @param language Locale of the player, e.g. `en_us`.
     */
    suspend fun aiCommand(prompt: String, language: String): String {
        requireVersion(LeaderOSVersion.V6_0_0, "integrations.aiCommand")
        val body = mapOf(
            "prompt" to prompt,
            "language" to language
        )
        return request("POST", "integrations/leaderos-ai/ai-command/ingame", form = body).data().decode<MessageDto>().message
    }
}
