package models

import kotlinx.serialization.Serializable

@Serializable
data class VoteLink(
    val id: String,
    val name: String,
    val link: String
)