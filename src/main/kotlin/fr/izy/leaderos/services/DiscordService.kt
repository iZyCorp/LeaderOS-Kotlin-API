package fr.izy.leaderos.services

import fr.izy.leaderos.internal.Service
import fr.izy.leaderos.internal.ServiceConfiguration
import fr.izy.leaderos.models.DiscordRole
import fr.izy.leaderos.models.DiscordSettings
import fr.izy.leaderos.models.DiscordUser

class DiscordService internal constructor(config: ServiceConfiguration) : Service(config) {

    /**
     * LeaderOS account linked to a Discord user, with its roles.
     */
    suspend fun user(discordId: String): DiscordUser {
        return get("integrations/discord/users/$discordId")
    }

    suspend fun settings(): DiscordSettings {
        return get("integrations/discord/settings")
    }

    /**
     * Website roles mapped to a Discord role.
     */
    suspend fun roles(): List<DiscordRole> {
        return get("integrations/discord/roles")
    }

    @Deprecated("Renamed", ReplaceWith("user(discordId)"))
    suspend fun getAccountByDiscordId(discordId: String): DiscordUser = user(discordId)
}
