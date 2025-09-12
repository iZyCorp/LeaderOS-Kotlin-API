package fr.izy.leaderos.services

import fr.izy.leaderos.internal.Service
import fr.izy.leaderos.internal.ServiceConfiguration
import fr.izy.leaderos.models.Category
import fr.izy.leaderos.models.Donation
import fr.izy.leaderos.models.OrderStatus
import fr.izy.leaderos.models.StoreProduct

class StoreService internal constructor(config: ServiceConfiguration) : Service(config) {

    suspend fun listing() : List<Category>? {
        val response: Map<String, List<Category>>? = super.get("store/listing")
        return response?.get("categories")
    }

    suspend fun products(): List<StoreProduct>? {
        return super.get("store/products")
    }

    suspend fun product(productId: Int? = null): StoreProduct? {
        return super.get("store/products/${productId}")
    }


    suspend fun categories() : List<Category>? {
        return super.get("store/categories")
    }

    suspend fun category(id: Int) : Category? {
        return super.get("store/categories/$id")
    }

    suspend fun buy(products: Array<Int>, userId: Int) : OrderStatus? {
        val body = mapOf(
            "products" to products.toString(),
            "amount" to userId.toString()
        )
        return super.post("store/buy", body)
    }

    /**
     * @param type `top-alltime`, `top-annual`, `top-monthly`, `top-daily`, `latest`
     */
    suspend fun donations(type: String, limit: Int) : List<Donation>? {
        return super.get("store/donations?type=$type&limit=$limit")
    }
}