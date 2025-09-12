package fr.izy.leaderos.models

import kotlinx.serialization.Serializable

@Serializable
data class VoteLink(
    val id: Int,
    val name: String,
    val link: String
)
