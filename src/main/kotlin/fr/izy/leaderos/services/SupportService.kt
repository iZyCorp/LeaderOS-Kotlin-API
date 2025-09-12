package fr.izy.leaderos.services

import fr.izy.leaderos.internal.IdDto
import fr.izy.leaderos.internal.Service
import fr.izy.leaderos.internal.ServiceConfiguration
import fr.izy.leaderos.models.QuickAnswer
import fr.izy.leaderos.models.Ticket
import fr.izy.leaderos.models.TicketCategory
import fr.izy.leaderos.models.TicketMessage
import fr.izy.leaderos.models.TicketStatus
import kotlinx.serialization.json.JsonElement

class SupportService internal constructor(config: ServiceConfiguration) : Service(config) {

    /**
     * Every ticket with its messages.
     */
    suspend fun tickets(): List<Ticket> {
        return get("support/tickets")
    }

    /**
     * A ticket with its category and messages.
     */
    suspend fun ticket(id: Int): Ticket {
        return get("support/tickets/$id")
    }

    /**
     * Opens a ticket and returns its ID.
     */
    suspend fun createTicket(
        accountId: Int,
        categoryId: Int,
        title: String,
        message: String,
        status: TicketStatus = TicketStatus.OPEN
    ): Int {
        require(status != TicketStatus.UNKNOWN) { "TicketStatus.UNKNOWN cannot be sent to LeaderOS" }

        val body = mapOf(
            "accountID" to accountId,
            "categoryID" to categoryId,
            "title" to title,
            "message" to message,
            "statusID" to status.id
        )
        return request("POST", "support/tickets", form = body).data().decode<IdDto>().id
    }

    suspend fun closeTicket(id: Int) {
        post<JsonElement>("support/tickets/$id/close")
    }

    suspend fun messages(ticketId: Int): List<TicketMessage> {
        return get("support/tickets/$ticketId/messages")
    }

    suspend fun message(ticketId: Int, messageId: Int): TicketMessage {
        return get("support/tickets/$ticketId/messages/$messageId")
    }

    /**
     * Adds a message to a ticket and returns the message ID.
     *
     * @param accountId Author of the message.
     * @param isAdmin `true` when the message is written by the staff.
     * @param discordUserId Discord author, when the message comes from Discord.
     */
    suspend fun reply(
        ticketId: Int,
        message: String,
        accountId: Int? = null,
        isAdmin: Boolean = false,
        discordUserId: String? = null
    ): Int {
        val body = mapOf(
            "message" to message,
            // The docs name it `accountID`, the official Discord bot sends `userID`
            "accountID" to accountId,
            "userID" to accountId,
            "isAdmin" to isAdmin,
            "discordUserID" to discordUserId
        )
        return request("POST", "support/tickets/$ticketId/messages", form = body).data().decode<IdDto>().id
    }

    suspend fun categories(): List<TicketCategory> {
        return get("support/categories")
    }

    suspend fun category(id: Int): TicketCategory {
        return request("GET", "support/categories/$id").firstIfArray().decode()
    }

    suspend fun quickAnswers(): List<QuickAnswer> {
        return get("support/answers")
    }

    suspend fun quickAnswer(id: Int): QuickAnswer {
        return get("support/answers/$id")
    }
}
