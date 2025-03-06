package models

import kotlinx.serialization.Serializable

@Serializable
data class StoreProduct(
    val id: String,
    val categoryID: String,
    val giveRoleID: String,
    val name: String,
    val imageID: String,
    val imageType: String,
    val details: String,
    val price: String,
    val discountedPrice: String,
    val discountExpiryDate: String,
    val duration: String,
    val stock: String,
    val priority: String,
    val isFeaturedProduct: String,
    val minecraftStatus: String,
    val minecraftTitle: String?,
    val minecraftDescription: String?,
    val minecraftItem: String,
    val minecraftItemModelID: String?,
    val requireOnlyOneProduct: String,
    val isActive: String,
    val creationDate: String
)