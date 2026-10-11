package fr.izy.leaderos.models

import kotlinx.serialization.Serializable

@Serializable
data class TicketCategory(
    val id: Int,
    val name: String
)
