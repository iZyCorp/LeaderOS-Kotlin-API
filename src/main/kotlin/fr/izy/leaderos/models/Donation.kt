@file:UseSerializers(ApiDecimalSerializer::class)

package fr.izy.leaderos.models

import fr.izy.leaderos.internal.ApiDateTimeSerializer
import fr.izy.leaderos.internal.ApiDecimalSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import java.math.BigDecimal
import java.time.LocalDateTime

@Serializable
data class Donation(
    @SerialName("display_name") val displayName: String? = null,
    val username: String,

    /**
     * Raw amount, e.g. `5.00`.
     */
    @SerialName("raw_total") val amount: BigDecimal = BigDecimal.ZERO,

    /**
     * Amount with its currency, e.g. `5.00 GBP`.
     */
    @SerialName("total") val formatted: String = "",
    val currency: String = "",
    @Serializable(with = ApiDateTimeSerializer::class) val date: LocalDateTime? = null
)

enum class DonationType(internal val apiValue: String) {
    TOP_ALLTIME("top-alltime"),
    TOP_ANNUAL("top-annual"),
    TOP_MONTHLY("top-monthly"),
    TOP_DAILY("top-daily"),
    LATEST("latest")
}
