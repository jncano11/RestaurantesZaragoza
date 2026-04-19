package com.example.restauranteszaragoza.model

import com.google.gson.annotations.SerializedName

// ─── USUARIO ─────────────────────────────────────────────────────────────────
data class Usuario(
    val id: Int? = null,
    val nombre: String = "",
    val apellidos: String = "",
    @SerializedName("nombreUsuario") val nombreUsuario: String = "",
    val email: String = "",
    val contrasena: String? = null,
    val telefono: String? = null,
    val rol: String = "usuario",          // "usuario" | "restaurante" | "admin"
    @SerializedName("foto_perfil") val fotoPerfil: String? = null
)

// Respuesta del login
data class LoginResponse(
    val success: Boolean,
    val message: String? = null,
    val usuario: Usuario? = null
)

// ─── RESTAURANTE ──────────────────────────────────────────────────────────────
data class Restaurante(
    // La API devuelve "restaurante_id", mapeamos a "id"
    @SerializedName("restaurante_id") val id: Int = 0,

    val nombre: String = "",
    val descripcion: String = "",
    val direccion: String = "",
    val ciudad: String = "",

    val latitud: Double = 0.0,
    val longitud: Double = 0.0,
    val telefono: String = "",

    @SerializedName("email_contacto") val emailContacto: String? = null,
    val categoria: String = "",

    // La API devuelve "15€" como String, lo guardamos como String
    @SerializedName("precio_medio") val precioMedio: String = "0€",

    @SerializedName("aforo_total") val aforoTotal: Int = 0,

    @SerializedName("usuario_id") val usuarioId: Int = 0,

    val activo: Int = 1,

    // Campos extra que devuelve detalle.php
    val horarios: List<Horario> = emptyList(),
    val menu: List<PlatoMenu> = emptyList(),
    val fotos: List<FotoRestaurante> = emptyList(),

    @SerializedName("rating_global") val ratingGlobal: Double = 0.0,
    @SerializedName("num_valoraciones") val numValoraciones: Int = 0
)

// ─── RESERVA ──────────────────────────────────────────────────────────────────
data class Reserva(
    val id: Int = 0,
    @SerializedName("usuario_id") val usuarioId: Int = 0,
    @SerializedName("restaurante_id") val restauranteId: Int = 0,
    @SerializedName("nombre_restaurante") val nombreRestaurante: String = "",
    @SerializedName("nombre_usuario") val nombreUsuario: String = "",
    val fecha: String = "",
    val hora: String = "",
    @SerializedName("num_personas") val numPersonas: Int = 1,
    val estado: String = "pendiente",   // pendiente|confirmada|cancelada|completada
    val notas: String = ""
)

// ─── VALORACION ───────────────────────────────────────────────────────────────
data class Valoracion(
    val id: Int = 0,
    @SerializedName("usuario_id") val usuarioId: Int = 0,
    @SerializedName("restaurante_id") val restauranteId: Int = 0,
    @SerializedName("nombre_usuario") val nombreUsuario: String = "",
    val puntuacion: Int = 5,
    val comentario: String = "",
    val fecha: String = ""
)

// ─── PLATO ────────────────────────────────────────────────────────────────────
data class Plato(
    val id: Int = 0,
    @SerializedName("categoria_id") val categoriaId: Int = 0,
    @SerializedName("restaurante_id") val restauranteId: Int = 0,
    val nombre: String = "",
    val descripcion: String = "",
    val precio: Double = 0.0,
    @SerializedName("foto_url") val fotoUrl: String = "",
    val disponible: Boolean = true,
    val alergenos: String = ""
)

// ─── HORARIO ──────────────────────────────────────────────────────────────────
data class Horario(
    @SerializedName("dia_semana") val diaSemana: Int = 0,
    @SerializedName("hora_apertura") val horaApertura: String = "",
    @SerializedName("hora_cierre") val horaCierre: String = "",
    val cerrado: Boolean = false
) {
    val nombreDia: String get() = when (diaSemana) {
        1 -> "Lunes"; 2 -> "Martes"; 3 -> "Miércoles"; 4 -> "Jueves"
        5 -> "Viernes"; 6 -> "Sábado"; 7 -> "Domingo"; else -> "Día $diaSemana"
    }
}

// ─── PLATO MENÚ (devuelto por detalle.php) ────────────────────────────────────
data class PlatoMenu(
    @SerializedName("categoria_nombre") val categoriaNombre: String = "",
    val nombre: String = "",
    val descripcion: String = "",
    val precio: Double = 0.0,
    @SerializedName("foto_url") val fotoUrl: String = "",
    val alergenos: String = ""
)

// ─── FOTO RESTAURANTE ─────────────────────────────────────────────────────────
data class FotoRestaurante(
    @SerializedName("url_foto") val urlFoto: String = "",
    val descripcion: String = "",
    @SerializedName("es_portada") val esPortada: Boolean = false
)

// ─── ESTADÍSTICAS ADMIN ───────────────────────────────────────────────────────
data class EstadisticasAdmin(
    @SerializedName("total_usuarios") val totalUsuarios: Int = 0,
    @SerializedName("total_restaurantes") val totalRestaurantes: Int = 0,
    @SerializedName("total_reservas") val totalReservas: Int = 0,
    @SerializedName("reservas_hoy") val reservasHoy: Int = 0,
    @SerializedName("ingresos_propinas") val ingresosPropinas: Double = 0.0
)

// ─── RESPUESTAS GENERICAS ─────────────────────────────────────────────────────
data class ApiResponse(
    val success: Boolean,
    val message: String = ""
)
