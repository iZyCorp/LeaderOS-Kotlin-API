package models

import kotlinx.serialization.Serializable

@Serializable
data class Ticket(
    val id: String,
    val accountID: String,
    val categoryID: String,
    val title: String,
    val statusID: String,
    val readStatus: String,
    val updateDate: String?,
    val creationDate: String,
    val categoryName: String
)