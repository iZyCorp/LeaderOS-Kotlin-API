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
data class BazaarServer(
    val id: Int,
    val name: String,
    val slug: String = "",
    @SerialName("imageID") val imageId: String? = null,
    val imageType: String? = null,
    val priority: Int = 0,
    val isActive: Boolean = true
)

/**
 * Item stored in a user's bazaar storage.
 */
@Serializable
data class BazaarItem(
    val id: Int,
    @SerialName("owner") val ownerId: Int,
    @SerialName("serverID") val serverId: Int,

    /**
     * Minecraft material, e.g. `DIAMOND_SWORD`.
     */
    @SerialName("itemID") val itemId: String,
    @SerialName("modelID") val modelId: String? = null,
    val name: String? = null,
    val lore: String? = null,
    val amount: Int = 1,
    val durability: Int = 0,
    val maxDurability: Int = 0,
    val enchantments: String? = null,

    /**
     * Serialized Bukkit ItemStack.
     */
    val base64: String,
    val description: String? = null,
    val price: BigDecimal = BigDecimal.ZERO,
    @SerialName("sold") val isSold: Boolean = false,
    @Serializable(with = ApiDateTimeSerializer::class) val creationDate: LocalDateTime? = null
)

/**
 * Item to add to a user's bazaar storage with `bazaar.addItem`.
 */
data class BazaarItemInput(
    val serverId: Int,

    /**
     * Minecraft material, e.g. `DIAMOND_SWORD`.
     */
    val itemId: String,
    val amount: Int,
    val durability: Int,
    val maxDurability: Int,

    /**
     * Serialized Bukkit ItemStack.
     */
    val base64: String,
    val name: String? = null,
    val lore: String? = null,
    val modelId: String? = null,
    val enchantments: String? = null,
    val price: Number? = null,
    val creationDate: LocalDateTime? = null
)
