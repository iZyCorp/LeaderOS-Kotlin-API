package fr.izy.leaderos.models

import kotlinx.serialization.Serializable

@Serializable
data class Order(
    val id: String,
    val accountID: String,
    val coupon: String?,
    val total: String,
    val discount: String,
    val subtotal: String,
    val paymentID: String,
    val paymentAPI: String,
    val type: String,
    val credit: String,
    val earnings: String,
    val cashback: String,
    val status: String,
    val creationDate: String
)