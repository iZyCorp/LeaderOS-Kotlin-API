package fr.izy.leaderos.services

import fr.izy.leaderos.LeaderOSVersion
import fr.izy.leaderos.internal.ListingDto
import fr.izy.leaderos.internal.Service
import fr.izy.leaderos.internal.ServiceConfiguration
import fr.izy.leaderos.models.BuyItem
import fr.izy.leaderos.models.Category
import fr.izy.leaderos.models.CommunityGoal
import fr.izy.leaderos.models.Donation
import fr.izy.leaderos.models.DonationType
import fr.izy.leaderos.models.Purchase
import fr.izy.leaderos.models.StoreProduct

class StoreService internal constructor(config: ServiceConfiguration) : Service(config) {

    /**
     * Categories with their subcategories and products.
     *
     * @param username When set, LeaderOS computes prices and restrictions for this user.
     */
    suspend fun listing(username: String? = null): List<Category> {
        val query = mapOf((if (isLegacy) "user" else "username") to username)
        return get<ListingDto>("store/listing", query).categories
    }

    suspend fun products(): List<StoreProduct> {
        return get("store/products")
    }

    suspend fun product(id: Int): StoreProduct {
        return get("store/products/$id")
    }

    suspend fun categories(): List<Category> {
        return get("store/categories")
    }

    suspend fun category(id: Int): Category {
        return get("store/categories/$id")
    }

    /**
     * Buys products with the user's credits.
     */
    suspend fun buy(userId: Int, items: List<BuyItem>): Purchase {
        require(items.isNotEmpty()) { "At least one product is required" }

        val body = buildMap<String, Any?> {
            put(if (isLegacy) "user" else "userID", userId)
            items.forEachIndexed { index, item ->
                put("products[$index][id]", item.productId)
                put("products[$index][quantity]", item.quantity)
            }
        }
        return request("POST", "store/buy", form = body).data().decode()
    }

    suspend fun buy(userId: Int, vararg items: BuyItem): Purchase = buy(userId, items.toList())

    /**
     * @param limit `null` to use the LeaderOS default.
     */
    suspend fun donations(type: DonationType, limit: Int? = null): List<Donation> {
        return get("store/donations", mapOf("type" to type.apiValue, "limit" to limit))
    }

    @Deprecated("Use donations(DonationType, limit)")
    suspend fun donations(type: String, limit: Int): List<Donation> {
        return get("store/donations", mapOf("type" to type, "limit" to limit))
    }

    suspend fun communityGoal(): CommunityGoal {
        requireVersion(LeaderOSVersion.V6_0_0, "store.communityGoal")
        return get("store/community-goal")
    }
}
