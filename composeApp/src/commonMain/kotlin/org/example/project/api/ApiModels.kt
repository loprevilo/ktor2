package org.example.project.api

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Long,
    val name: String,
    val email: String? = null,
    val username: String? = null
)

@Serializable
data class Post(
    val id: Long,
    val userId: Long,
    val title: String,
    val body: String? = null
)
@Serializable
data class ProvinciaResponse(
    val CODPROV: Int,
    val NOMBRE_PROVINCIA: String,
    val email: String? = null,
    val username: String? = null,
    val phone: String? = null,
    val website: String? = null
)


typealias UserList = List<UserResponse>
