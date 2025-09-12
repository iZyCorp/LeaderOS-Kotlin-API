package fr.izy.leaderos.internal

import okhttp3.OkHttpClient

interface ServiceConfiguration {
    val baseUrl: String
    val client: OkHttpClient
    fun getAuthHeader(): String
}