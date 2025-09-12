package fr.izy.leaderos.services

import fr.izy.leaderos.internal.Service
import fr.izy.leaderos.internal.ServiceConfiguration
import fr.izy.leaderos.models.Order
import fr.izy.leaderos.models.Role
import fr.izy.leaderos.models.Ticket
import fr.izy.leaderos.models.User

class UserService internal constructor(config: ServiceConfiguration) : Service(config) {

    suspend fun profile(id: Int): User? {
        return super.get("users/$id")
    }

    suspend fun profile(username: String): User? {
        return super.get("users/$username")
    }

    suspend fun roles(id: Int): List<Role>? {
        return super.get("users/$id/roles")
    }

    suspend fun roles(username: String): List<Role>? {
        return super.get("users/$username/roles")
    }

    suspend fun tickets(id: Int): List<Ticket>? {
        return super.get("users/$id/tickets")
    }

    suspend fun tickets(username: String): List<Ticket>? {
        return super.get("users/$username/tickets")
    }

    suspend fun orders(id: Int): List<Order>? {
        return super.get("users/$id/orders")
    }

    suspend fun orders(username: String): List<Order>? {
        return super.get("users/$username/orders")
    }
}