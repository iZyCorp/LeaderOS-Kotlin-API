@file:UseSerializers(ApiBooleanSerializer::class)

package fr.izy.leaderos.models

import fr.izy.leaderos.internal.ApiBooleanSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

/**
 * Store category.
 */
@Serializable
data class Category(
    val id: Int,

    /**
     * `0` for a root category.
     */
    @SerialName("parentID") val parentId: Int = 0,
    val name: String,
    val slug: String = "",
    @SerialName("imageID") val imageId: String? = null,
    val imageType: String? = null,
    val priority: Int = 0,
    val minecraftStatus: Boolean = false,
    val minecraftTitle: String? = null,
    val minecraftDescription: String? = null,
    val minecraftItem: String? = null,
    @SerialName("minecraftItemModelID") val minecraftItemModelId: String? = null,
    val isActive: Boolean = true,
    val isCumulative: Boolean = false,
    val subcategories: List<Category> = emptyList(),
    val products: List<StoreProduct> = emptyList()
)
