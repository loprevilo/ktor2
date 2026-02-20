package org.example.project.api

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
class ApiClient(val httpClient: HttpClient) {
    suspend inline fun <reified T> get(
        url: String,
        headers: Map<String, String> = emptyMap()
    ): T {
        return httpClient.get(url) {
            headers.forEach { (key, value) -> header(key, value) }
        }.body()
    }
    suspend inline fun <reified B, reified R> post(
        url: String,
        body: B,
        headers: Map<String, String> = emptyMap()
    ): R {
        return httpClient.post(url) {
            contentType(ContentType.Application.Json)
            headers.forEach { (key, value) -> header(key, value) }
            setBody(body)
        }.body()
    }
    suspend inline fun <reified R> post(
        url: String,
        headers: Map<String, String> = emptyMap()
    ): R {
        return httpClient.post(url) {
            contentType(ContentType.Application.Json)
            headers.forEach { (key, value) -> header(key, value) }
        }.body()
    }
    suspend inline fun <reified B, reified R> put(
        url: String,
        body: B,
        headers: Map<String, String> = emptyMap()
    ): R {
        return httpClient.put(url) {
            contentType(ContentType.Application.Json)
            headers.forEach { (key, value) -> header(key, value) }
            setBody(body)
        }.body()
    }
    suspend inline fun <reified R> delete(
        url: String,
        headers: Map<String, String> = emptyMap()
    ): R? {
        return runCatching {
            httpClient.delete(url) {
                headers.forEach { (key, value) -> header(key, value) }
            }.body<R>()
        }.getOrNull()
    }
}

expect fun createHttpClient(): HttpClient
