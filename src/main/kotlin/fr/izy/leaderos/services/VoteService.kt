package fr.izy.leaderos.services

import fr.izy.leaderos.internal.Service
import fr.izy.leaderos.internal.ServiceConfiguration
import fr.izy.leaderos.models.VoteLink

class VoteService internal constructor(config: ServiceConfiguration) : Service(config) {

    /**
     * Fetch vote uri defined in leaderOS. If a player is specified it will add an extra parameter to autocomplete username
     */
    suspend fun links(username: String? = null): List<VoteLink>? {
        return super.get("vote-links${username?.let { "?username=$it" } ?: ""}")
    }
}