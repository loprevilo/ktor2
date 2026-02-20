package org.example.project.api

object ApiExample {

    private const val BASE_URL = "https://api.el-tiempo.net/json/v3"

    suspend fun fetchProvincias(): List<UserResponse> {
        val client = ApiClient(createHttpClient())
        return client.get("$BASE_URL/users")
    }

    suspend fun fetchUser(id: Long): UserResponse {
        val client = ApiClient(createHttpClient())
        return client.get("$BASE_URL/users/$id")
    }

    suspend fun fetchPosts(): List<Post> {
        val client = ApiClient(createHttpClient())
        return client.get("$BASE_URL/posts")
    }
}
