package fr.izy.leaderos.models

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,
    val uuid: String?,
    val username: String,
    val realname: String,
    val email: String,
    val lastlogin: String,
    val x: String,
    val y: String,
    val z: String,
    val world: String,
    val isLogged: String,
    val credit: String,
    val isVerified: String,
    val authStatus: String,
    val creationIP: String,
    val creationDate: String
)