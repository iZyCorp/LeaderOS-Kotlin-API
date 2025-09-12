package fr.izy.leaderos

import fr.izy.leaderos.internal.ServiceConfiguration
import fr.izy.leaderos.services.AuthService
import fr.izy.leaderos.services.CreditService
import fr.izy.leaderos.services.DiscordService
import fr.izy.leaderos.services.StoreService
import fr.izy.leaderos.services.UserService
import fr.izy.leaderos.services.VoteService
import okhttp3.OkHttpClient


class LeaderOS(
    /**
     * Ex: https://myWebsite.com
     */
    baseUrl: String,

    /**
     * The API key can be found in the LeaderOS settings.
     */
    apiKey: String,

    client: OkHttpClient = OkHttpClient()
) {

    private val config = ApiConfiguration(baseUrl, apiKey, client)

    val credits: CreditService = CreditService(config)
    val store: StoreService = StoreService(config)
    val user: UserService = UserService(config)
    val discord: DiscordService = DiscordService(config)
    val vote: VoteService = VoteService(config)
    val auth: AuthService = AuthService(config)

    /**
     * Internal object used to hold necessary data
     */
    private class ApiConfiguration(
        override val baseUrl: String,
        private val apiKey: String,
        override val client: OkHttpClient
    ) : ServiceConfiguration {
        override fun getAuthHeader(): String = apiKey
    }
    
}