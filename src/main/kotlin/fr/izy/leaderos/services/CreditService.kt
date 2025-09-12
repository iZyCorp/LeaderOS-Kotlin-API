package fr.izy.leaderos.services

import fr.izy.leaderos.internal.Service
import fr.izy.leaderos.internal.ServiceConfiguration
import kotlinx.serialization.json.JsonObject

class CreditService internal constructor(config: ServiceConfiguration) : Service(config) {

    suspend fun oldShow(username: String) : JsonObject? {
        return super.get("credits/$username")
    }

    suspend fun oldShow(id: Int) : JsonObject? {
        return super.get("credits/$id")
    }

    suspend fun show(username: String) : JsonObject? {
        return super.get("credits?username=$username")
    }

    suspend fun show(id: Int) : JsonObject? {
        return super.get("credits?id=$id")
    }

    suspend fun add(targetUsername: String, amount: Double): JsonObject? {
        val body = mapOf(
            "target_username" to targetUsername,
            "amount" to amount.toString()
        )
        return super.post("credits/add", body)
    }

    /**
     * If you are using leaderOS 5.5.9 use this
     */
    suspend fun oldAdd(targetUsername: String, amount: Double): JsonObject? {
        val body = mapOf(
            "amount" to amount.toString()
        )
        return super.post("credits/$targetUsername/add", body)
    }

    suspend fun remove(targetUsername: String, amount: Double): JsonObject? {
        val body = mapOf(
            "target_username" to targetUsername,
            "amount" to amount.toString()
        )
        return super.post("credits/remove", body)
    }

    /**
     * If you are using leaderOS 5.5.9 use this
     */
    suspend fun oldRemove(targetUsername: String, amount: Double): JsonObject? {
        val body = mapOf(
            "amount" to amount.toString()
        )
        return super.post("credits/$targetUsername/remove", body)
    }


    suspend fun send(senderUsername: String, targetUsername: String, amount: Double) : JsonObject? {
        val body = mapOf(
            "sender_username" to senderUsername,
            "target_username" to targetUsername,
            "amount" to amount.toString()
        )
        return super.post("credits/send", body)
    }

    /**
     * If you are using leaderOS 5.5.9 use this
     */
    suspend fun oldSend(senderUsername: String, targetUsername: String, amount: Double) : JsonObject? {
        val body = mapOf(
            "target_username" to targetUsername,
            "amount" to amount.toString()
        )
        return super.post("credits/$senderUsername/send", body)
    }

    suspend fun oldSet(targetUsername: String, amount: Double) : JsonObject? {
        val body = mapOf(
            "amount" to amount.toString()
        )
        return super.post("credits/$targetUsername/set", body)
    }

    suspend fun set(targetUsername: String, amount: Double) : JsonObject? {
        val body = mapOf(
            "target_username" to targetUsername.toString(),
            "amount" to amount.toString()
        )
        return super.post("credits/set", body)
    }


}