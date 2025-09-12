package fr.izy.leaderos

import fr.izy.leaderos.internal.ServiceConfiguration
import fr.izy.leaderos.services.AuthService
import fr.izy.leaderos.services.BazaarService
import fr.izy.leaderos.services.CreditService
import fr.izy.leaderos.services.DiscordService
import fr.izy.leaderos.services.IntegrationService
import fr.izy.leaderos.services.StoreService
import fr.izy.leaderos.services.SupportService
import fr.izy.leaderos.services.UserService
import fr.izy.leaderos.services.VoteService
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
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

    client: OkHttpClient = OkHttpClient(),

    /**
     * Version of the LeaderOS website, used to call the right endpoints when they changed between versions.
     */
    val version: LeaderOSVersion = LeaderOSVersion.LATEST
) {

    private val config = ApiConfiguration(baseUrl.toHttpUrl(), apiKey, client, version)

    val credits: CreditService = CreditService(config)
    val store: StoreService = StoreService(config)
    val user: UserService = UserService(config)
    val discord: DiscordService = DiscordService(config)
    val vote: VoteService = VoteService(config)
    val auth: AuthService = AuthService(config)
    val bazaar: BazaarService = BazaarService(config)
    val support: SupportService = SupportService(config)
    val integrations: IntegrationService = IntegrationService(config)

    /**
     * Internal object used to hold necessary data
     */
    private class ApiConfiguration(
        override val baseUrl: HttpUrl,
        private val apiKey: String,
        override val client: OkHttpClient,
        override val version: LeaderOSVersion
    ) : ServiceConfiguration {
        override fun getAuthHeader(): String = apiKey
    }

}
