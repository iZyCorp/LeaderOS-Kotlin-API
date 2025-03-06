import internal.ServiceConfiguration
import okhttp3.OkHttpClient
import services.AuthService
import services.CreditService
import services.DiscordService
import services.StoreService
import services.UserService
import services.VoteService


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
     * Internal object used to hold needed data
     */
    private class ApiConfiguration(
        override val baseUrl: String,
        private val apiKey: String,
        override val client: OkHttpClient
    ) : ServiceConfiguration {
        override fun getAuthHeader(): String = apiKey
    }
    
}