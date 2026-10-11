package fr.izy.leaderos.services

import fr.izy.leaderos.internal.CreditsDto
import fr.izy.leaderos.internal.Service
import fr.izy.leaderos.internal.ServiceConfiguration
import fr.izy.leaderos.models.CreditBalance
import fr.izy.leaderos.models.CreditTransfer
import java.math.BigDecimal

class CreditService internal constructor(config: ServiceConfiguration) : Service(config) {

    suspend fun show(username: String): CreditBalance {
        val response: CreditsDto =
            if (isLegacy) get("credits/$username") else get("credits", mapOf("username" to username))
        return response.balance()
    }

    suspend fun show(id: Int): CreditBalance {
        val response: CreditsDto =
            if (isLegacy) get("credits/$id") else get("credits", mapOf("id" to id))
        return response.balance()
    }

    /**
     * @return the target's balance after the operation.
     */
    suspend fun add(targetUsername: String, amount: Number): CreditBalance =
        update("add", targetUsername, amount)

    /**
     * @return the target's balance after the operation.
     */
    suspend fun remove(targetUsername: String, amount: Number): CreditBalance =
        update("remove", targetUsername, amount)

    /**
     * @return the target's balance after the operation.
     */
    suspend fun set(targetUsername: String, amount: Number): CreditBalance =
        update("set", targetUsername, amount)

    suspend fun send(senderUsername: String, targetUsername: String, amount: Number): CreditTransfer {
        val response: CreditsDto = if (isLegacy) {
            // Older wrapper versions sent `target_username`, the official 5.x plugin sent `target`
            post("credits/$senderUsername/send", mapOf("target" to targetUsername, "target_username" to targetUsername, "amount" to amount))
        } else {
            post("credits/send", mapOf("sender_username" to senderUsername, "target_username" to targetUsername, "amount" to amount))
        }

        val sender = response.currentRawCredits?.let { CreditBalance(it, response.currentCredits.orEmpty(), response.currency.orEmpty()) }
            ?: show(senderUsername)
        val target = response.targetCurrentRawCredits?.let { CreditBalance(it, response.targetCurrentCredits.orEmpty(), response.currency.orEmpty()) }
            ?: show(targetUsername)
        return CreditTransfer(sender, target)
    }

    private suspend fun update(operation: String, targetUsername: String, amount: Number): CreditBalance {
        val response: CreditsDto = if (isLegacy) {
            post("credits/$targetUsername/$operation", mapOf("amount" to amount))
        } else {
            post("credits/$operation", mapOf("target_username" to targetUsername, "amount" to amount))
        }

        // LeaderOS 5.x does not always return the new balance
        return response.currentRawCredits?.let { CreditBalance(it, response.currentCredits.orEmpty(), response.currency.orEmpty()) }
            ?: show(targetUsername)
    }

    private fun CreditsDto.balance() = CreditBalance(rawCredits ?: BigDecimal.ZERO, credits.orEmpty(), currency.orEmpty())

    @Deprecated("Configure the version once: LeaderOS(url, key, version = LeaderOSVersion.V5_5_9)", ReplaceWith("show(username)"), DeprecationLevel.ERROR)
    suspend fun oldShow(username: String): CreditBalance = show(username)

    @Deprecated("Configure the version once: LeaderOS(url, key, version = LeaderOSVersion.V5_5_9)", ReplaceWith("show(id)"), DeprecationLevel.ERROR)
    suspend fun oldShow(id: Int): CreditBalance = show(id)

    @Deprecated("Configure the version once: LeaderOS(url, key, version = LeaderOSVersion.V5_5_9)", ReplaceWith("add(targetUsername, amount)"), DeprecationLevel.ERROR)
    suspend fun oldAdd(targetUsername: String, amount: Double): CreditBalance = add(targetUsername, amount)

    @Deprecated("Configure the version once: LeaderOS(url, key, version = LeaderOSVersion.V5_5_9)", ReplaceWith("remove(targetUsername, amount)"), DeprecationLevel.ERROR)
    suspend fun oldRemove(targetUsername: String, amount: Double): CreditBalance = remove(targetUsername, amount)

    @Deprecated("Configure the version once: LeaderOS(url, key, version = LeaderOSVersion.V5_5_9)", ReplaceWith("send(senderUsername, targetUsername, amount)"), DeprecationLevel.ERROR)
    suspend fun oldSend(senderUsername: String, targetUsername: String, amount: Double): CreditTransfer = send(senderUsername, targetUsername, amount)

    @Deprecated("Configure the version once: LeaderOS(url, key, version = LeaderOSVersion.V5_5_9)", ReplaceWith("set(targetUsername, amount)"), DeprecationLevel.ERROR)
    suspend fun oldSet(targetUsername: String, amount: Double): CreditBalance = set(targetUsername, amount)
}
