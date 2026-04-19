package com.example.restauranteszaragoza.model

import com.google.gson.annotations.SerializedName

data class PerfilStats(
    val usuario: Usuario? = null,
    @SerializedName("total_reservas")        val totalReservas: Int = 0,
    @SerializedName("restaurantes_visitados") val restaurantesVisitados: Int = 0,
    @SerializedName("total_personas")        val totalPersonas: Int = 0,
    val confirmadas: Int = 0,
    val pendientes: Int = 0,
    val canceladas: Int = 0,
    val completadas: Int = 0,
    @SerializedName("total_valoraciones")    val totalValoraciones: Int = 0,
    @SerializedName("media_puntuacion")      val mediaPuntuacion: Double = 0.0,
    @SerializedName("ultimas_reservas")      val ultimasReservas: List<Reserva> = emptyList()
)
