package fr.izy.leaderos.services

import fr.izy.leaderos.internal.Service
import fr.izy.leaderos.internal.ServiceConfiguration
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject

class AuthService internal constructor(config: ServiceConfiguration) : Service(config) {

    suspend fun login(username: String, password: String) : String? {
        val body = mapOf(
            "username" to username,
            "password" to password
        )
        val obj: JsonObject? = super.post("auth/login", body)
        return obj?.get("data")?.jsonObject?.get("token").toString()
    }

    suspend fun register(username: String, password: String, email: String, ip: String) : String? {
        val body = mapOf(
            "username" to username,
            "password" to password,
            "email" to email,
            "ip" to ip
        )
        val obj: JsonObject? =  super.post("auth/register", body)
        return obj?.get("data")?.jsonObject?.get("token").toString()
    }

    suspend fun sessions(token: String) : Boolean? {
        return super.get("auth/sessions/$token")
    }

    suspend fun sessions(userId: Int, ip: String, expires: String) : JsonObject? {
        val body = mapOf(
            "userID" to userId.toString(),
            "ip" to ip,
            "expires" to expires
        )
        return super.post("auth/sessions", body)
    }
}