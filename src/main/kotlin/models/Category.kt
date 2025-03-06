package models

import kotlinx.serialization.Serializable

@Serializable
data class Category(
    val id: String,
    val parentID: String,
    val name: String,
    val slug: String,
    val imageID: String,
    val imageType: String,
    val priority: String,
    val minecraftStatus: String,
    val minecraftTitle: String?,
    val minecraftDescription: String?,
    val minecraftItem: String,
    val minecraftItemModelID: String?,
    val isActive: String,
    val isCumulative: String,
    val subcategories: List<Category> = emptyList(),
    val products: List<StoreProduct> = emptyList()
)