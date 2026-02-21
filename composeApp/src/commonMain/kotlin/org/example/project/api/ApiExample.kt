package org.example.project.api

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

object ApiExample {

    private const val BASE_URL = "https://api.el-tiempo.net/json/v3"

    suspend fun fetchProvincias(): ProvinciaResponse {
        val client = ApiClient(createHttpClient())
        return client.get("$BASE_URL/provincias")
    }

    suspend fun fetchMunicipios(codprov: String): MunicipiosResponse {
        val client = ApiClient(createHttpClient())
        return client.get("$BASE_URL/provincias/$codprov/municipios")
    }

    suspend fun fetchWeather(codprov: String, codigoine: String): WeatherDetail {
        val client = ApiClient(createHttpClient())
        val ineLimpio = codigoine.take(5)
        return client.get("$BASE_URL/provincias/$codprov/municipios/$ineLimpio")
    }
}
