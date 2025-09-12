package fr.izy.leaderos.models

import kotlinx.serialization.Serializable

@Serializable
data class OrderStatus(
    val status: Boolean,
    val orderID: String,
    val credits: String,
    val products: List<StoreProduct>,
    val cashback: String,
    val total: String,
    val subtotal: String,
    val discount: String
)