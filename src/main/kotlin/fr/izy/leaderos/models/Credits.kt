package fr.izy.leaderos.models

import java.math.BigDecimal

data class CreditBalance(
    /**
     * Raw amount, e.g. `10.25`.
     */
    val amount: BigDecimal,

    /**
     * Amount with its currency, e.g. `10.25 USD`.
     */
    val formatted: String,
    val currency: String
)

/**
 * Balances of both accounts after `credits.send`.
 */
data class CreditTransfer(
    val sender: CreditBalance,
    val target: CreditBalance
)
