package services

import internal.Service
import internal.ServiceConfiguration
import models.User

class DiscordService internal constructor(config: ServiceConfiguration) : Service(config) {

    suspend fun getAccountByDiscordId(discordId: String) : User? {
        return super.get("integrations/discord/users/$discordId")
    }
}