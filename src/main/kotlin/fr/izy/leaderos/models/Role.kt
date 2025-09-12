package fr.izy.leaderos.models

import fr.izy.leaderos.internal.ApiDateTimeSerializer
import fr.izy.leaderos.internal.BlankAsNullSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

/**
 * A role owned by a user.
 */
@Serializable
data class Role(
    val id: Int,
    val name: String,
    val slug: String,
    val priority: Int = 0,
    @SerialName("discordRoleID") @Serializable(with = BlankAsNullSerializer::class) val discordRoleId: String? = null,

    /**
     * `null` when the role is permanent.
     */
    @Serializable(with = ApiDateTimeSerializer::class) val expiryDate: LocalDateTime? = null
) {
    val isPermanent: Boolean
        get() = expiryDate == null
}
