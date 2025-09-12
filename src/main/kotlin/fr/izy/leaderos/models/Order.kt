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
data class Order(
    val id: Int,
    @SerialName("accountID") val accountId: Int,
    val coupon: String? = null,
    val total: BigDecimal = BigDecimal.ZERO,
    val discount: BigDecimal = BigDecimal.ZERO,
    val subtotal: BigDecimal = BigDecimal.ZERO,
    val earnings: BigDecimal = BigDecimal.ZERO,
    val cashback: BigDecimal = BigDecimal.ZERO,
    val credit: BigDecimal = BigDecimal.ZERO,
    val type: Int = 0,
    val status: Int = 0,
    @SerialName("paymentAPI") val paymentApi: String? = null,
    @SerialName("paymentID") val paymentId: String? = null,
    @Serializable(with = ApiDateTimeSerializer::class) val creationDate: LocalDateTime? = null
)
