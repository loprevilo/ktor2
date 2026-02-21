package org.example.project.api

import kotlinx.serialization.Serializable

@Serializable
data class Provincias(
    val CODPROV: String,
    val NOMBRE_PROVINCIA: String,
    val CODAUTON: String,
    val COMUNIDAD_CIUDAD_AUTONOMA: String,
    val CAPITAL_PROVINCIA: String
)

@Serializable
data class Post(
    val CODPROV: String,
    val NOMBRE_PROVINCIA: String,
    val CODAUTON: String,
    val COMUNIDAD_CIUDAD_AUTONOMA: String,
    val CAPITAL_PROVINCIA: String
)

@Serializable
data class ProvinciaResponse(
    val Provincias: List<Provincias>
)

@Serializable
data class WeatherDetail(
    val municipio: MunicipioInfo,
    val temperatura_actual: String,
    val humedad: String,
    val estado_cielo: EstadoCielo,
    val amanecer: String,
    val ocaso: String
)

@Serializable
data class MunicipioInfo(
    val NOMBRE: String
)

@Serializable
data class EstadoCielo(
    val description: String
)

@Serializable
data class Municipio(
    val CODIGOINE: String,
    val NOMBRE: String,
    val CODPROV: String
)

@Serializable
data class MunicipiosResponse(
    val municipios: List<Municipio>
)

typealias ProvinciasList = List<ProvinciaResponse>
typealias WeatherDetailList = List<WeatherDetail>