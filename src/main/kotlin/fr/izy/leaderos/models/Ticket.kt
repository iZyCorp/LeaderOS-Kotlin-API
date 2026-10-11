@file:UseSerializers(ApiBooleanSerializer::class)

package fr.izy.leaderos.models

import fr.izy.leaderos.internal.ApiBooleanSerializer
import fr.izy.leaderos.internal.ApiDateTimeSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import java.time.LocalDateTime

@Serializable
data class Ticket(
    val id: Int,
    @SerialName("accountID") val accountId: Int,
    @SerialName("categoryID") val categoryId: Int,
    val title: String,
    @SerialName("statusID") val statusId: Int = TicketStatus.OPEN.id,
    val readStatus: Boolean = false,
    @Serializable(with = ApiDateTimeSerializer::class) val updateDate: LocalDateTime? = null,
    @Serializable(with = ApiDateTimeSerializer::class) val creationDate: LocalDateTime? = null,

    /**
     * Sent by list endpoints (`user.tickets`, `support.tickets`).
     */
    val categoryName: String? = null,

    /**
     * Sent by `support.ticket(id)`.
     */
    val category: TicketCategory? = null,

    /**
     * Empty on `user.tickets`.
     */
    val messages: List<TicketMessage> = emptyList()
) {
    val status: TicketStatus
        get() = TicketStatus.fromId(statusId)
}

enum class TicketStatus(val id: Int) {
    OPEN(1),
    ANSWERED(2),
    USER_REPLY(3),
    CLOSED(4),

    /**
     * Status not known by this wrapper, see [Ticket.statusId].
     */
    UNKNOWN(-1);

    companion object {
        fun fromId(id: Int): TicketStatus = entries.firstOrNull { it.id == id } ?: UNKNOWN
    }
}
