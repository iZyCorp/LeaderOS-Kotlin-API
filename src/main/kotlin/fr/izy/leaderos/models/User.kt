@file:UseSerializers(ApiBooleanSerializer::class, ApiDecimalSerializer::class)

package fr.izy.leaderos.models

import fr.izy.leaderos.internal.ApiBooleanSerializer
import fr.izy.leaderos.internal.ApiDateTimeSerializer
import fr.izy.leaderos.internal.ApiDecimalSerializer
import fr.izy.leaderos.internal.LenientStringMapSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import java.math.BigDecimal
import java.time.LocalDateTime

@Serializable
data class User(
    val id: Int,
    val username: String,

    /**
     * Username with its original casing (e.g. `iZyy_` when [username] is `izyy_`).
     */
    val realname: String? = null,
    val email: String? = null,
    val uuid: String? = null,
    val credit: BigDecimal = BigDecimal.ZERO,
    val isVerified: Boolean = false,
    val authStatus: Int = 0,
    @SerialName("creationIP") val creationIp: String? = null,
    @Serializable(with = ApiDateTimeSerializer::class) val creationDate: LocalDateTime? = null,

    /**
     * Linked third-party accounts keyed by provider, e.g. `discord` → Discord user ID.
     */
    @Serializable(with = LenientStringMapSerializer::class) val linkedAccounts: Map<String, String> = emptyMap()
)
