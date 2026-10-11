package fr.izy.leaderos.services

import fr.izy.leaderos.internal.Service
import fr.izy.leaderos.internal.ServiceConfiguration
import fr.izy.leaderos.models.BazaarItem
import fr.izy.leaderos.models.BazaarItemInput
import fr.izy.leaderos.models.BazaarServer
import kotlinx.serialization.json.JsonElement

class BazaarService internal constructor(config: ServiceConfiguration) : Service(config) {

    suspend fun servers(): List<BazaarServer> {
        return get("bazaar/servers")
    }

    /**
     * Items in a user's storage.
     *
     * @param serverId `null` to get the items of every server.
     */
    suspend fun items(userId: Int, serverId: Int? = null): List<BazaarItem> {
        return get("bazaar/storages/$userId/items", mapOf("serverID" to serverId))
    }

    suspend fun addItem(userId: Int, item: BazaarItemInput) {
        val body = mapOf(
            "owner" to userId.takeIf { isLegacy },
            "serverID" to item.serverId,
            "itemID" to item.itemId,
            "modelID" to item.modelId,
            "name" to item.name,
            "lore" to item.lore,
            "amount" to item.amount,
            "durability" to item.durability,
            "maxDurability" to item.maxDurability,
            "enchantments" to item.enchantments,
            "base64" to item.base64,
            "price" to item.price,
            "creationDate" to item.creationDate
        )
        post<JsonElement>("bazaar/storages/$userId/items", body)
    }

    suspend fun removeItem(userId: Int, itemId: Int) {
        delete<JsonElement>("bazaar/storages/$userId/items/$itemId")
    }
}
