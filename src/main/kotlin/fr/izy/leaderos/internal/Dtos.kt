@file:UseSerializers(ApiDecimalSerializer::class)

package fr.izy.leaderos.internal

import fr.izy.leaderos.models.Category
import fr.izy.leaderos.models.SessionState
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import java.math.BigDecimal

// Response shapes that are reshaped before being exposed.

@Serializable
internal class CreditsDto(
    val credits: String? = null,
    @SerialName("raw_credits") val rawCredits: BigDecimal? = null,
    @SerialName("current_credits") val currentCredits: String? = null,
    @SerialName("current_raw_credits") val currentRawCredits: BigDecimal? = null,
    @SerialName("target_current_credits") val targetCurrentCredits: String? = null,
    @SerialName("target_current_raw_credits") val targetCurrentRawCredits: BigDecimal? = null,
    val currency: String? = null
)

@Serializable
internal class ListingDto(val categories: List<Category> = emptyList())

@Serializable
internal class IdDto(val id: Int)

@Serializable
internal class TokenDto(val token: String)

@Serializable
internal class MessageDto(val message: String)

@Serializable
internal class GameSessionDto(
    val state: SessionState = SessionState.UNKNOWN,
    val token: String? = null,
    val user: GameSessionUserDto? = null
)

@Serializable
internal class GameSessionUserDto(val realname: String? = null)
