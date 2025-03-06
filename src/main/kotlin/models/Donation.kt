package models

import kotlinx.serialization.Serializable

@Serializable
data class Donation(
    val displayName: String? = null,
    val username: String,
    val total: String,
    val rawTotal: String? = null,
    val currency: String,
    val date: String
)