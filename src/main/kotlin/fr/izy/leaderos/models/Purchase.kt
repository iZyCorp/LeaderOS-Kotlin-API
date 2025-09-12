@file:UseSerializers(ApiDecimalSerializer::class)

package fr.izy.leaderos.models

import fr.izy.leaderos.internal.ApiDecimalSerializer
import fr.izy.leaderos.internal.LenientStringMapSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import java.math.BigDecimal

/**
 * Result of `store.buy`.
 */
@Serializable
data class Purchase(
    @SerialName("orderID") val orderId: Int,

    /**
     * Credits left on the buyer's account after the purchase.
     */
    val credits: BigDecimal = BigDecimal.ZERO,
    val products: List<PurchasedProduct> = emptyList(),
    val cashback: BigDecimal = BigDecimal.ZERO,
    val total: BigDecimal = BigDecimal.ZERO,
    val subtotal: BigDecimal = BigDecimal.ZERO,
    val discount: BigDecimal = BigDecimal.ZERO
)

@Serializable
data class PurchasedProduct(
    val id: Int,
    val quantity: Int = 1,
    @Serializable(with = LenientStringMapSerializer::class) val variables: Map<String, String> = emptyMap()
)

/**
 * A product to buy with `store.buy`.
 */
data class BuyItem(
    val productId: Int,
    val quantity: Int = 1
)
