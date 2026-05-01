package com.example.restauranteszaragoza.network

import com.example.restauranteszaragoza.model.*
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

// ─── URLs DE LA API ───────────────────────────────────────────────────────────
object Api {

    //const val BASE_URL = "http://10.0.2.2/restaurantes_api/"        // Emulador
    const val BASE_URL = "http://192.168.1.72/restaurantes_api/"  // Dispositivo físico
    // Usuarios
    const val LOGIN    = "${BASE_URL}usuarios/login.php"
    const val REGISTER = "${BASE_URL}usuarios/register.php"
    const val PERFIL   = "${BASE_URL}usuarios/perfil.php"
    const val USUARIOS_LISTAR = "${BASE_URL}admin/usuarios_listar.php"
    const val USUARIOS_TOGGLE = "${BASE_URL}admin/usuario_toggle.php"

    // Restaurantes
    const val RESTAURANTES_LISTAR = "${BASE_URL}restaurantes/listar.php"
    const val RESTAURANTES_DETALLE= "${BASE_URL}restaurantes/detalle.php"
    const val RESTAURANTE_EDITAR  = "${BASE_URL}restaurantes/editar.php"
    const val RESTAURANTE_TOGGLE       = "${BASE_URL}admin/restaurante_toggle.php"
    const val MI_RESTAURANTE           = "${BASE_URL}restaurantes/mi_restaurante.php"
    const val ADMIN_RESTAURANTES_LISTAR= "${BASE_URL}admin/restaurantes_listar.php"

    // Reservas
    const val RESERVAS_USUARIO     = "${BASE_URL}reservas/mis_reservas.php"
    const val RESERVAS_RESTAURANTE = "${BASE_URL}reservas/por_restaurante.php"
    const val RESERVAS_TODAS       = "${BASE_URL}admin/reservas_todas.php"
    const val RESERVA_CREAR        = "${BASE_URL}reservas/crear.php"
    const val RESERVA_CANCELAR     = "${BASE_URL}reservas/cancelar.php"
    const val RESERVA_CONFIRMAR    = "${BASE_URL}reservas/confirmar.php"

    // Valoraciones
    const val VALORACIONES_RESTAURANTE = "${BASE_URL}valoraciones/por_restaurante.php"
    const val VALORACION_CREAR         = "${BASE_URL}valoraciones/crear.php"

    // Admin
    const val ADMIN_ESTADISTICAS = "${BASE_URL}admin/estadisticas.php"
}

// ─── INTERFAZ RETROFIT ────────────────────────────────────────────────────────
interface ApiService {

    // ── Autenticación ──
    @POST("usuarios/login.php")
    suspend fun login(@Body body: Map<String, String>): Response<LoginResponse>

    @POST("usuarios/register.php")
    suspend fun registrar(@Body body: Map<String, String>): ApiResponse

    @POST("restaurantes/register_restaurante.php")
    suspend fun registrarRestaurante(@Body body: Map<String, String>): ApiResponse

    @POST("restaurantes/completar_restaurante.php")
    suspend fun completarRestaurante(@Body body: Map<String, String>): ApiResponse

    @POST("restaurantes/solicitar_aprobacion.php")
    suspend fun solicitarAprobacion(@Body body: Map<String, String>): ApiResponse

    @GET("admin/restaurantes_pendientes.php")
    suspend fun restaurantesPendientes(): List<RestaurantePendiente>

    @POST("admin/restaurante_aprobar.php")
    suspend fun aprobarRechazarRestaurante(@Body body: Map<String, String>): ApiResponse

    // ── Restaurantes ──
    @GET("restaurantes/listar.php")
    suspend fun listarRestaurantes(
        @Query("categoria") categoria: String? = null,
        @Query("q") busqueda: String? = null
    ): List<Restaurante>

    @GET("restaurantes/detalle.php")
    suspend fun detalleRestaurante(@Query("id") id: Int): Restaurante

    @GET("restaurantes/mi_restaurante.php")
    suspend fun miRestaurante(@Query("usuario_id") usuarioId: Int): Restaurante


    @GET("usuarios/perfil_stats.php")
    suspend fun perfilStats(@Query("usuario_id") usuarioId: Int): PerfilStats

    // ── Menú ──
    @GET("menu/platos.php")
    suspend fun listarMenu(@Query("restaurante_id") restauranteId: Int): List<PlatoDetalle>

    @GET("menu/listar_categorias.php")
    suspend fun listarCategorias(@Query("restaurante_id") restauranteId: Int): List<MenuCategoria>

    @POST("menu/crear_categoria.php")
    suspend fun crearCategoria(@Body body: Map<String, String>): CategoriaResponse

    @POST("menu/plato_crear.php")
    suspend fun crearPlato(@Body body: Map<String, String>): ApiResponse

    @POST("menu/plato_eliminar.php")
    suspend fun eliminarPlato(@Body body: Map<String, String>): ApiResponse

    @POST("restaurantes/crear.php")
    suspend fun crearRestaurante(@Body body: Map<String, String>): ApiResponse

    @POST("restaurantes/editar.php")
    suspend fun editarRestaurante(@Body body: Map<String, String>): ApiResponse

    // ── Reservas ──
    @GET("reservas/mis_reservas.php")
    suspend fun misReservas(@Query("usuario_id") usuarioId: Int): List<Reserva>

    @GET("reservas/por_restaurante.php")
    suspend fun reservasPorRestaurante(@Query("restaurante_id") restauranteId: Int): List<Reserva>

    @POST("reservas/crear.php")
    suspend fun crearReserva(@Body body: Map<String, String>): ApiResponse

    @POST("reservas/cancelar.php")
    suspend fun cancelarReserva(@Body body: Map<String, String>): ApiResponse

    @POST("reservas/confirmar.php")
    suspend fun confirmarReserva(@Body body: Map<String, String>): ApiResponse

    // ── Valoraciones ──
    @GET("valoraciones/por_restaurante.php")
    suspend fun valoracionesRestaurante(@Query("restaurante_id") id: Int): List<Valoracion>

    @POST("valoraciones/crear.php")
    suspend fun crearValoracion(@Body body: Map<String, String>): ApiResponse

    // ── Admin ──
    @GET("admin/estadisticas.php")
    suspend fun estadisticasAdmin(): EstadisticasAdmin

    @GET("admin/usuarios_listar.php")
    suspend fun listarUsuarios(): List<Usuario>

    @GET("admin/restaurantes_listar.php")
    suspend fun listarRestaurantesAdmin(): List<Restaurante>

    @POST("admin/usuario_toggle.php")
    suspend fun toggleUsuario(@Body body: Map<String, String>): ApiResponse

    @GET("admin/reservas_todas.php")
    suspend fun todasReservas(): List<Reserva>

    @POST("admin/restaurante_toggle.php")
    suspend fun toggleRestaurante(@Body body: Map<String, String>): ApiResponse

    @POST("admin/usuario_cambiar_rol.php")
    suspend fun cambiarRolUsuario(@Body body: Map<String, String>): ApiResponse

    @POST("admin/usuario_eliminar.php")
    suspend fun eliminarUsuario(@Body body: Map<String, String>): ApiResponse
}

// Acepta 0/1 además de true/false para campos Boolean de MySQL
private val booleanAdapter = object : TypeAdapter<Boolean>() {
    override fun write(out: JsonWriter, value: Boolean?) { out.value(value) }
    override fun read(reader: JsonReader): Boolean {
        return when (reader.peek()) {
            JsonToken.BOOLEAN -> reader.nextBoolean()
            JsonToken.NUMBER  -> reader.nextInt() != 0
            else              -> { reader.skipValue(); false }
        }
    }
}

// ─── CLIENTE RETROFIT ─────────────────────────────────────────────────────────
object RetrofitClient {
    val instancia: ApiService by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        val gson = GsonBuilder()
            .registerTypeAdapter(Boolean::class.java, booleanAdapter)
            .registerTypeAdapter(Boolean::class.javaPrimitiveType, booleanAdapter)
            .create()

        Retrofit.Builder()
            .baseUrl(Api.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ApiService::class.java)
    }
}
