@file:UseSerializers(ApiBooleanSerializer::class, ApiDecimalSerializer::class)

package fr.izy.leaderos.models

import fr.izy.leaderos.internal.ApiBooleanSerializer
import fr.izy.leaderos.internal.ApiDateTimeSerializer
import fr.izy.leaderos.internal.ApiDecimalSerializer
import fr.izy.leaderos.internal.BlankAsNullSerializer
import fr.izy.leaderos.internal.NonBlankStringListSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * LeaderOS account linked to a Discord user.
 */
@Serializable
data class DiscordUser(
    val id: Int,
    val username: String,
    val realname: String? = null,
    val email: String? = null,
    val credit: BigDecimal = BigDecimal.ZERO,
    val avatar: String? = null,
    val isVerified: Boolean = false,
    val authStatus: Int = 0,
    @SerialName("creationIP") val creationIp: String? = null,
    @Serializable(with = ApiDateTimeSerializer::class) val creationDate: LocalDateTime? = null,
    @SerialName("discordUserID") @Serializable(with = BlankAsNullSerializer::class) val discordUserId: String? = null,
    val roles: List<Role> = emptyList()
)

@Serializable
data class DiscordSettings(
    @SerialName("guildID") @Serializable(with = BlankAsNullSerializer::class) val guildId: String? = null,
    val roleSyncingStatus: Boolean = false,
    @SerialName("syncedRoleID") @Serializable(with = BlankAsNullSerializer::class) val syncedRoleId: String? = null,
    val setNicknameStatus: Boolean = false,
    val ticketStatus: Boolean = false,
    @SerialName("ticketCategoryID") @Serializable(with = BlankAsNullSerializer::class) val ticketCategoryId: String? = null,
    @SerialName("ticketStaffRoleIDs") @Serializable(with = NonBlankStringListSerializer::class) val ticketStaffRoleIds: List<String> = emptyList()
)

/**
 * Website role mapped to a Discord role.
 */
@Serializable
data class DiscordRole(
    val id: Int,
    val name: String,
    val slug: String,
    @SerialName("usernameCSS") val usernameCss: String? = null,
    @SerialName("badgeCSS") val badgeCss: String? = null,
    val priority: Int = 0,
    @SerialName("discordRoleID") @Serializable(with = BlankAsNullSerializer::class) val discordRoleId: String? = null
)
