@file:UseSerializers(ApiBooleanSerializer::class, ApiDecimalSerializer::class)

package fr.izy.leaderos.models

import fr.izy.leaderos.internal.ApiBooleanSerializer
import fr.izy.leaderos.internal.ApiDateTimeSerializer
import fr.izy.leaderos.internal.ApiDecimalSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import java.math.BigDecimal
import java.time.LocalDateTime

@Serializable
data class StoreProduct(
    val id: Int,
    @SerialName("categoryID") val categoryId: Int = 0,

    /**
     * `0` when the product does not give a role.
     */
    @SerialName("giveRoleID") val giveRoleId: Int = 0,
    val name: String,
    @SerialName("imageID") val imageId: String? = null,
    val imageType: String? = null,

    /**
     * HTML description.
     */
    val details: String? = null,
    val price: BigDecimal = BigDecimal.ZERO,
    val discountedPrice: BigDecimal? = null,

    /**
     * `null` when the discount never expires.
     */
    @Serializable(with = ApiDateTimeSerializer::class) val discountExpiryDate: LocalDateTime? = null,

    /**
     * Duration in days, `0` for a permanent product.
     */
    val duration: Int = 0,
    val stock: Int? = null,
    val priority: Int = 0,
    val isFeaturedProduct: Boolean = false,
    val minecraftStatus: Boolean = false,
    val minecraftTitle: String? = null,
    val minecraftDescription: String? = null,
    val minecraftItem: String? = null,
    @SerialName("minecraftItemModelID") val minecraftItemModelId: String? = null,
    val requireOnlyOneProduct: Boolean = false,
    val isActive: Boolean = true,
    @Serializable(with = ApiDateTimeSerializer::class) val creationDate: LocalDateTime? = null,

    // The fields below are computed by LeaderOS 6.x on `store.listing(username)`.

    /**
     * Final price for the user, discounts included.
     */
    val total: BigDecimal? = null,
    val totalFormatted: String? = null,
    val originalPrice: BigDecimal? = null,
    val originalPriceFormatted: String? = null,
    val discount: BigDecimal? = null,
    val discountPercent: Int? = null,

    /**
     * `true` when the user cannot buy this product (missing required product, linked account…).
     */
    val restricted: Boolean? = null
)
