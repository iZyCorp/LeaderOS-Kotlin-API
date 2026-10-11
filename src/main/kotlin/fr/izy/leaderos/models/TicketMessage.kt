package fr.izy.leaderos.models

import fr.izy.leaderos.internal.ApiDateTimeSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class TicketMessage(
    val id: Int,
    @SerialName("accountID") val accountId: Int,

    /**
     * Not sent when the message is nested in a [Ticket].
     */
    @SerialName("supportID") val ticketId: Int? = null,

    /**
     * Markdown (user side) or HTML (staff side).
     */
    val message: String,

    /**
     * Where the message was written: `1` from the user side, `2` from the staff side.
     */
    val writeLocation: Int = 0,
    @Serializable(with = ApiDateTimeSerializer::class) val creationDate: LocalDateTime? = null
)
