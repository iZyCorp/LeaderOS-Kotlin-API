package models

import kotlinx.serialization.Serializable

@Serializable
data class Role(
    val id: String,
    val name: String,
    val slug: String,
    val priority: String,
    val discordRoleID: String?,
    val expiryDate: String
)