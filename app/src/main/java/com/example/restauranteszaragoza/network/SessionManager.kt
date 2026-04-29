package com.example.restauranteszaragoza.network

import com.example.restauranteszaragoza.model.Usuario

/**
 * Singleton que mantiene la sesión del usuario mientras la app está abierta.
 * Se inicializa al hacer login y se limpia al hacer logout.
 */
object SessionManager {
    var usuarioActual: Usuario? = null

    val estaLogueado get() = usuarioActual != null
    val rolActual get() = usuarioActual?.rol ?: "usuario"
    val esAdmin get() = rolActual == "admin"
    val esRestaurante get() = rolActual == "restaurante"
    val esUsuario get() = rolActual == "usuario"
    val usuarioId get() = usuarioActual?.id?.takeIf { it > 0 } ?: 0

    fun cerrarSesion() { usuarioActual = null }
}
