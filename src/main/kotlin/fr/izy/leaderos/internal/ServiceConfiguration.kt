package fr.izy.leaderos.internal

import fr.izy.leaderos.LeaderOSVersion
import okhttp3.HttpUrl
import okhttp3.OkHttpClient

internal interface ServiceConfiguration {
    val baseUrl: HttpUrl
    val client: OkHttpClient
    val version: LeaderOSVersion
    fun getAuthHeader(): String
}
