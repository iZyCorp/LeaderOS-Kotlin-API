package fr.izy.leaderos.services

import fr.izy.leaderos.internal.Service
import fr.izy.leaderos.internal.ServiceConfiguration
import fr.izy.leaderos.models.User

class DiscordService internal constructor(config: ServiceConfiguration) : Service(config) {

    suspend fun getAccountByDiscordId(discordId: String) : User? {
        return super.get("integrations/discord/users/$discordId")
    }
}