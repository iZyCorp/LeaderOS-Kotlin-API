package fr.izy.leaderos.services

import fr.izy.leaderos.internal.Service
import fr.izy.leaderos.internal.ServiceConfiguration
import fr.izy.leaderos.models.Order
import fr.izy.leaderos.models.Role
import fr.izy.leaderos.models.Ticket
import fr.izy.leaderos.models.User
import java.time.LocalDateTime

class UserService internal constructor(config: ServiceConfiguration) : Service(config) {

    suspend fun profile(id: Int): User =
        if (isLegacy) get("users/$id") else get("users", mapOf("userID" to id))

    suspend fun profile(username: String): User =
        if (isLegacy) get("users/$username") else get("users", mapOf("username" to username))

    suspend fun roles(id: Int): List<Role> {
        return get("users/$id/roles")
    }

    suspend fun roles(username: String): List<Role> {
        return get("users/$username/roles")
    }

    /**
     * Gives a role to a user and returns the user's roles.
     *
     * @param expiryDate `null` for a permanent role.
     */
    suspend fun addRole(userId: Int, roleId: Int, expiryDate: LocalDateTime? = null): List<Role> {
        val body = mapOf(
            "roleID" to roleId,
            "expiryDate" to expiryDate
        )
        return post("users/$userId/roles", body)
    }

    /**
     * Updates a role owned by a user and returns the user's roles.
     *
     * @param expiryDate `null` to leave the expiry date unchanged.
     */
    suspend fun updateRole(userId: Int, roleId: Int, expiryDate: LocalDateTime? = null): List<Role> {
        val body = mapOf(
            "roleID" to roleId,
            "expiryDate" to expiryDate
        )
        return patch("users/$userId/roles/$roleId", body)
    }

    /**
     * Removes a role from a user and returns the user's remaining roles.
     */
    suspend fun removeRole(userId: Int, roleId: Int): List<Role> {
        return delete("users/$userId/roles/$roleId")
    }

    suspend fun tickets(id: Int): List<Ticket> {
        return get("users/$id/tickets")
    }

    suspend fun tickets(username: String): List<Ticket> {
        return get("users/$username/tickets")
    }

    suspend fun orders(id: Int): List<Order> {
        return get("users/$id/orders")
    }

    suspend fun orders(username: String): List<Order> {
        return get("users/$username/orders")
    }
}
