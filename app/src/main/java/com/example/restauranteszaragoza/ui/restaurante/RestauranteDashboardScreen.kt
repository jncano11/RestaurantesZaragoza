package com.example.restauranteszaragoza.ui.restaurante

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.restauranteszaragoza.model.CategoriaResponse
import com.example.restauranteszaragoza.model.MenuCategoria
import com.example.restauranteszaragoza.model.PlatoDetalle
import com.example.restauranteszaragoza.model.Reserva
import com.example.restauranteszaragoza.model.Restaurante
import com.example.restauranteszaragoza.network.RetrofitClient
import com.example.restauranteszaragoza.network.SessionManager
import kotlinx.coroutines.launch
import retrofit2.HttpException

private val ACCENT    = Color(0xFFFFA726)
private val CARD_BG   = Color(0xFF1E1A14)
private val BG_COLORS = listOf(Color(0xFF1A0A00), Color(0xFF2C1810), Color(0xFF1A0A00))

// Categorías de restaurante disponibles para creación
private val CATEGORIAS_REST = listOf(
    "Parrilla", "Sushi", "Pasta", "Alta cocina", "Mariscos",
    "Mexicana", "Vegana", "Americana", "Fusión", "Sidreria", "Kebab", "Otros"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestauranteDashboardScreen(onLogout: () -> Unit) {
    val scope         = rememberCoroutineScope()
    var reservas      by remember { mutableStateOf<List<Reserva>>(emptyList()) }
    var miRestaurante by remember { mutableStateOf<Restaurante?>(null) }
    var platos        by remember { mutableStateOf<List<PlatoDetalle>>(emptyList()) }
    var categorias    by remember { mutableStateOf<List<MenuCategoria>>(emptyList()) }
    var loading        by remember { mutableStateOf(true) }
    var isRefreshing   by remember { mutableStateOf(false) }
    // null = aún no sabemos, true = no tiene restaurante, false = tiene restaurante
    var sinRestaurante by remember { mutableStateOf<Boolean?>(null) }
    var errorMsg       by remember { mutableStateOf<String?>(null) }
    var tabIndex      by remember { mutableStateOf(0) }
    var snackMsg      by remember { mutableStateOf<String?>(null) }
    val snackState    = remember { SnackbarHostState() }

    val usuario = SessionManager.usuarioActual

    fun cargarDatos() {
        loading = true; errorMsg = null; sinRestaurante = null
        scope.launch {
            // Cargar el restaurante — solo aquí se decide si tiene o no restaurante
            val r = try {
                RetrofitClient.instancia.miRestaurante(SessionManager.usuarioId)
            } catch (e: HttpException) {
                if (e.code() == 404) sinRestaurante = true
                else errorMsg = "Error del servidor (${e.code()}). Comprueba la conexión."
                loading = false
                return@launch
            } catch (e: Exception) {
                errorMsg = "No se pudo cargar tu restaurante.\nComprueba la conexión."
                loading = false
                return@launch
            }

            miRestaurante = r
            sinRestaurante = false

            // El resto de llamadas fallan en silencio — no afectan al estado del restaurante
            try { reservas = RetrofitClient.instancia.reservasPorRestaurante(r.id) } catch (_: Exception) {}
            try { platos = RetrofitClient.instancia.listarMenu(r.id) } catch (_: Exception) {}
            try { categorias = RetrofitClient.instancia.listarCategorias(r.id) } catch (_: Exception) {}

            loading = false
            isRefreshing = false
        }
    }

    LaunchedEffect(Unit) { cargarDatos() }
    LaunchedEffect(snackMsg) { snackMsg?.let { snackState.showSnackbar(it); snackMsg = null } }

    Scaffold(
        snackbarHost = { SnackbarHost(snackState) },
        containerColor = Color(0xFF1A0A00)
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(BG_COLORS)).padding(padding)) {
            Column(Modifier.fillMaxSize()) {

                // ── Header siempre visible ────────────────────────────────────
                Box(
                    modifier = Modifier.fillMaxWidth()
                        .background(Brush.horizontalGradient(listOf(Color(0xFF7B3F00), Color(0xFFBF360C))))
                        .padding(20.dp)
                ) {
                    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                        Column {
                            Text("Panel de Restaurante 🍽️", color = Color.White.copy(0.7f), fontSize = 13.sp)
                            Text("¡Hola, ${usuario?.nombre ?: "Chef"}!", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 22.sp)
                            miRestaurante?.let { Text(it.nombre, color = ACCENT, fontSize = 14.sp, fontWeight = FontWeight.Medium) }
                            if (sinRestaurante == true) Text("Crea tu restaurante para empezar", color = ACCENT.copy(0.7f), fontSize = 13.sp)
                        }
                        IconButton(
                            onClick = onLogout,
                            modifier = Modifier.background(Color.Black.copy(0.3f), CircleShape).size(42.dp)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Logout, "Salir", tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                    }
                }

                PullToRefreshBox(
                    isRefreshing = isRefreshing,
                    onRefresh = { isRefreshing = true; cargarDatos() },
                    modifier = Modifier.weight(1f)
                ) {
                when {
                    loading -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = ACCENT)
                            Spacer(Modifier.height(12.dp))
                            Text("Cargando...", color = Color.Gray)
                        }
                    }

                    // ── BUG FIX: pantalla de creación de restaurante ──────────
                    sinRestaurante == true -> {
                        CrearRestauranteScreen(
                            onCreado = {
                                snackMsg = "🎉 ¡Restaurante creado con éxito!"
                                cargarDatos()
                            }
                        )
                    }

                    errorMsg != null -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
                            Icon(Icons.Default.ErrorOutline, null, tint = Color(0xFFEF5350), modifier = Modifier.size(64.dp))
                            Spacer(Modifier.height(16.dp))
                            Text(errorMsg ?: "", color = Color.LightGray, fontSize = 15.sp, textAlign = TextAlign.Center)
                            Spacer(Modifier.height(20.dp))
                            Button(onClick = { cargarDatos() }, colors = ButtonDefaults.buttonColors(containerColor = ACCENT), shape = RoundedCornerShape(12.dp)) {
                                Icon(Icons.Default.Refresh, null, tint = Color.Black)
                                Spacer(Modifier.width(8.dp))
                                Text("Reintentar", color = Color.Black, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    else -> {
                        // ── Tarjeta info restaurante ──────────────────────────
                        miRestaurante?.let { rest ->
                            Card(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = CARD_BG)
                            ) {
                                Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Restaurant, null, tint = ACCENT, modifier = Modifier.size(20.dp))
                                    Spacer(Modifier.width(10.dp))
                                    Column(Modifier.weight(1f)) {
                                        Text(rest.nombre, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                        Text("${rest.categoria} · ${rest.precioMedio}", color = Color.Gray, fontSize = 12.sp)
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Star, null, tint = Color(0xFFFFB300), modifier = Modifier.size(14.dp))
                                        Text(" ${rest.ratingGlobal}", color = Color.LightGray, fontSize = 13.sp)
                                    }
                                }
                            }
                        }

                        // ── Banner estado aprobación ──────────────────────────
                        miRestaurante?.let { rest ->
                            when {
                                rest.aprobado == 1 -> { /* aprobado: no mostrar nada */ }
                                rest.solicitado == 1 -> {
                                    Card(
                                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
                                        shape = RoundedCornerShape(14.dp),
                                        colors = CardDefaults.cardColors(containerColor = Color(0xFF2D2200))
                                    ) {
                                        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.HourglassTop, null, tint = Color(0xFFFFA726), modifier = Modifier.size(22.dp))
                                            Spacer(Modifier.width(10.dp))
                                            Column(Modifier.weight(1f)) {
                                                Text("Pendiente de aprobación", color = Color(0xFFFFA726), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                                Text("Un administrador revisará tu solicitud pronto.", color = Color.Gray, fontSize = 12.sp)
                                            }
                                        }
                                    }
                                }
                                else -> {
                                    Card(
                                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
                                        shape = RoundedCornerShape(14.dp),
                                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A2E))
                                    ) {
                                        Column(Modifier.padding(14.dp)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.Info, null, tint = Color(0xFF7C4DFF), modifier = Modifier.size(22.dp))
                                                Spacer(Modifier.width(10.dp))
                                                Column(Modifier.weight(1f)) {
                                                    Text("Restaurante en borrador", color = Color(0xFF7C4DFF), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                                    Text("Tu restaurante no es visible para los usuarios. Completa los datos y envía la solicitud.", color = Color.Gray, fontSize = 12.sp)
                                                }
                                            }
                                            Spacer(Modifier.height(10.dp))
                                            Button(
                                                onClick = {
                                                    scope.launch {
                                                        try {
                                                            val resp = RetrofitClient.instancia.solicitarAprobacion(
                                                                mapOf("restaurante_id" to rest.id.toString())
                                                            )
                                                            if (resp.success) {
                                                                snackMsg = "✅ Solicitud enviada al administrador"
                                                                cargarDatos()
                                                            } else {
                                                                snackMsg = "❌ ${resp.message}"
                                                            }
                                                        } catch (_: Exception) { snackMsg = "❌ Error de conexión" }
                                                    }
                                                },
                                                modifier = Modifier.fillMaxWidth(),
                                                shape = RoundedCornerShape(10.dp),
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C4DFF))
                                            ) {
                                                Icon(Icons.Default.Send, null, tint = Color.White, modifier = Modifier.size(16.dp))
                                                Spacer(Modifier.width(8.dp))
                                                Text("Enviar petición al administrador", color = Color.White, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        val pendientes  = reservas.count { it.estado == "pendiente" }
                        val confirmadas = reservas.count { it.estado == "confirmada" || it.estado == "esperando_usuario" }
                        val hoy         = reservas.count { it.fecha == java.time.LocalDate.now().toString() }

                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).padding(bottom = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            StatCard("Pendientes",  pendientes.toString(),  Icons.Default.HourglassTop, Color(0xFFFFA726), Modifier.weight(1f))
                            StatCard("Confirmadas", confirmadas.toString(), Icons.Default.CheckCircle,  Color(0xFF43A047), Modifier.weight(1f))
                            StatCard("Hoy",         hoy.toString(),         Icons.Default.Today,        Color(0xFF1E88E5), Modifier.weight(1f))
                        }

                        TabRow(selectedTabIndex = tabIndex, containerColor = Color.Transparent, contentColor = ACCENT) {
                            listOf("Todas", "Pendientes", "Hoy", "Menú").forEachIndexed { idx, label ->
                                Tab(selected = tabIndex == idx, onClick = { tabIndex = idx }) {
                                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(10.dp)) {
                                        if (label == "Menú") Icon(Icons.Default.MenuBook, null, modifier = Modifier.size(14.dp), tint = if (tabIndex == idx) ACCENT else Color.Gray)
                                        if (label == "Menú") Spacer(Modifier.width(4.dp))
                                        Text(label, color = if (tabIndex == idx) ACCENT else Color.Gray, fontSize = 13.sp)
                                    }
                                }
                            }
                        }

                        if (tabIndex == 3) {
                            MenuTab(
                                restauranteId = miRestaurante?.id ?: 0,
                                platos = platos,
                                categorias = categorias,
                                onPlatoEliminado = { platoId ->
                                    scope.launch {
                                        try {
                                            RetrofitClient.instancia.eliminarPlato(mapOf(
                                                "plato_id"       to platoId.toString(),
                                                "restaurante_id" to (miRestaurante?.id ?: 0).toString()
                                            ))
                                            platos = RetrofitClient.instancia.listarMenu(miRestaurante?.id ?: 0)
                                            snackMsg = "🗑️ Plato eliminado"
                                        } catch (_: Exception) { snackMsg = "❌ Error al eliminar" }
                                    }
                                },
                                onPlatoCreado = {
                                    scope.launch {
                                        val rid = miRestaurante?.id ?: 0
                                        platos = RetrofitClient.instancia.listarMenu(rid)
                                        try { categorias = RetrofitClient.instancia.listarCategorias(rid) } catch (_: Exception) {}
                                        snackMsg = "✅ Plato añadido al menú"
                                    }
                                },
                                onCategoriaCreada = { nueva ->
                                    categorias = categorias + nueva
                                }
                            )
                        } else {
                            val listaFiltrada = when (tabIndex) {
                                1    -> reservas.filter { it.estado == "pendiente" }
                                2    -> reservas.filter { it.fecha == java.time.LocalDate.now().toString() }
                                else -> reservas
                            }

                            if (listaFiltrada.isEmpty()) {
                                Box(Modifier.fillMaxSize().padding(top = 48.dp), Alignment.TopCenter) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(Icons.Default.EventAvailable, null, tint = Color.Gray, modifier = Modifier.size(56.dp))
                                        Spacer(Modifier.height(12.dp))
                                        Text("Sin reservas en este filtro", color = Color.Gray)
                                    }
                                }
                            } else {
                                LazyColumn(
                                    contentPadding = PaddingValues(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    items(listaFiltrada) { reserva ->
                                        val restauranteId = miRestaurante?.id ?: 0
                                        ReservaRestauranteCard(
                                            reserva = reserva,
                                            onConfirmar = {
                                                scope.launch {
                                                    try {
                                                        RetrofitClient.instancia.confirmarReserva(mapOf("reserva_id" to reserva.id.toString()))
                                                        reservas = RetrofitClient.instancia.reservasPorRestaurante(restauranteId)
                                                        snackMsg = "✅ Reserva confirmada"
                                                    } catch (_: Exception) { snackMsg = "❌ Error de conexión" }
                                                }
                                            },
                                            onCancelar = {
                                                scope.launch {
                                                    try {
                                                        RetrofitClient.instancia.cancelarReserva(mapOf("reserva_id" to reserva.id.toString()))
                                                        reservas = RetrofitClient.instancia.reservasPorRestaurante(restauranteId)
                                                        snackMsg = "Reserva cancelada"
                                                    } catch (_: Exception) { snackMsg = "❌ Error de conexión" }
                                                }
                                            }
                                        )
                                    }
                                    item { Spacer(Modifier.height(24.dp)) }
                                }
                            }
                        }
                    }
                }
                } // PullToRefreshBox
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Pantalla de creación de restaurante (BUG FIX)
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun CrearRestauranteScreen(onCreado: () -> Unit) {
    val scope       = rememberCoroutineScope()
    var email       by remember { mutableStateOf("") }
    var precioMedio by remember { mutableStateOf("") }
    var aforo       by remember { mutableStateOf("") }
    var loading     by remember { mutableStateOf(false) }
    var error       by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(80.dp).background(ACCENT.copy(0.15f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.AddBusiness, null, tint = ACCENT, modifier = Modifier.size(40.dp))
        }
        Spacer(Modifier.height(16.dp))
        Text("¡Solo faltan algunos pasos!", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 22.sp)
        Text(
            "Tu restaurante ya está registrado. Rellena los siguientes campos para completar el perfil.",
            color = Color.Gray, fontSize = 13.sp, textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp, bottom = 28.dp)
        )

        OutlinedTextField(
            value = aforo, onValueChange = { aforo = it; error = null },
            label = { Text("Aforo (número de comensales)") }, singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp), colors = fieldColors()
        )
        Spacer(Modifier.height(14.dp))

        OutlinedTextField(
            value = email, onValueChange = { email = it; error = null },
            label = { Text("Email de contacto") }, singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp), colors = fieldColors()
        )
        Spacer(Modifier.height(14.dp))

        OutlinedTextField(
            value = precioMedio, onValueChange = { precioMedio = it; error = null },
            label = { Text("Precio medio (€, ej: 15)") }, singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp), colors = fieldColors()
        )

        error?.let {
            Spacer(Modifier.height(12.dp))
            Card(shape = RoundedCornerShape(10.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF2E1A1A))) {
                Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Warning, null, tint = Color(0xFFEF5350), modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(it, color = Color(0xFFEF5350), fontSize = 13.sp)
                }
            }
        }

        Spacer(Modifier.height(28.dp))

        Button(
            onClick = {
                if (aforo.toIntOrNull() == null || aforo.toIntOrNull()!! <= 0) {
                    error = "Introduce un aforo válido"; return@Button
                }
                loading = true
                scope.launch {
                    try {
                        val resp = RetrofitClient.instancia.completarRestaurante(mapOf(
                            "usuario_id"     to SessionManager.usuarioId.toString(),
                            "aforo_total"    to (aforo.toIntOrNull() ?: 0).toString(),
                            "email_contacto" to email,
                            "precio_medio"   to (precioMedio.toFloatOrNull() ?: 0f).toString()
                        ))
                        onCreado()
                    } catch (e: Exception) {
                        error = "Error de conexión"
                    } finally { loading = false }
                }
            },
            modifier = Modifier.fillMaxWidth().height(54.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ACCENT),
            enabled = !loading
        ) {
            if (loading) CircularProgressIndicator(modifier = Modifier.size(22.dp), color = Color.Black, strokeWidth = 2.dp)
            else {
                Icon(Icons.Default.Check, null, tint = Color.Black, modifier = Modifier.size(22.dp))
                Spacer(Modifier.width(10.dp))
                Text("Completar mi restaurante", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
        Spacer(Modifier.height(32.dp))
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Tab Menú, platos y resto de composables (sin cambios)
// ─────────────────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MenuTab(
    restauranteId: Int,
    platos: List<PlatoDetalle>,
    categorias: List<MenuCategoria>,
    onPlatoEliminado: (Int) -> Unit,
    onPlatoCreado: () -> Unit,
    onCategoriaCreada: (MenuCategoria) -> Unit
) {
    var showAddSheet by remember { mutableStateOf(false) }

    Box(Modifier.fillMaxSize()) {
        if (platos.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize().padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(Icons.Default.MenuBook, null, tint = Color.Gray, modifier = Modifier.size(64.dp))
                Spacer(Modifier.height(16.dp))
                Text("Tu menú está vacío", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text("Pulsa + para añadir tus primeros platos", color = Color.Gray, fontSize = 14.sp, textAlign = TextAlign.Center)
            }
        } else {
            val platosPorCategoria = platos.groupBy { it.categoriaNombre }
            LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                platosPorCategoria.forEach { (cat, lista) ->
                    item {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 8.dp)) {
                            HorizontalDivider(modifier = Modifier.weight(1f), color = ACCENT.copy(0.3f))
                            Surface(color = ACCENT.copy(0.12f), shape = RoundedCornerShape(8.dp), modifier = Modifier.padding(horizontal = 8.dp)) {
                                Text(cat, color = ACCENT, fontWeight = FontWeight.Bold, fontSize = 13.sp, modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp))
                            }
                            HorizontalDivider(modifier = Modifier.weight(1f), color = ACCENT.copy(0.3f))
                        }
                    }
                    items(lista) { plato ->
                        PlatoGestionCard(plato = plato, onEliminar = { onPlatoEliminado(plato.id) })
                    }
                }
                item { Spacer(Modifier.height(80.dp)) }
            }
        }
        FloatingActionButton(
            onClick = { showAddSheet = true },
            modifier = Modifier.align(Alignment.BottomEnd).padding(24.dp),
            containerColor = ACCENT, contentColor = Color.Black
        ) {
            Icon(Icons.Default.Add, "Añadir plato", modifier = Modifier.size(28.dp))
        }
    }

    if (showAddSheet) {
        AñadirPlatoSheet(
            restauranteId = restauranteId,
            categorias = categorias,
            onDismiss = { showAddSheet = false },
            onSuccess = { showAddSheet = false; onPlatoCreado() },
            onCategoriaCreada = onCategoriaCreada
        )
    }
}

@Composable
private fun PlatoGestionCard(plato: PlatoDetalle, onEliminar: () -> Unit) {
    var showConfirm by remember { mutableStateOf(false) }
    if (showConfirm) {
        AlertDialog(
            onDismissRequest = { showConfirm = false },
            containerColor = Color(0xFF1E1A14),
            title = { Text("Eliminar plato", color = Color.White, fontWeight = FontWeight.Bold) },
            text  = { Text("¿Seguro que quieres eliminar \"${plato.nombre}\" del menú?", color = Color.LightGray) },
            confirmButton = { TextButton(onClick = { showConfirm = false; onEliminar() }) { Text("Eliminar", color = Color(0xFFEF5350), fontWeight = FontWeight.Bold) } },
            dismissButton = { TextButton(onClick = { showConfirm = false }) { Text("Cancelar", color = Color.Gray) } }
        )
    }
    Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = CARD_BG), modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(plato.nombre, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                if (plato.descripcion.isNotBlank()) { Spacer(Modifier.height(2.dp)); Text(plato.descripcion, color = Color.Gray, fontSize = 12.sp, maxLines = 2) }
                if (plato.alergenos.isNotBlank()) { Spacer(Modifier.height(3.dp)); Text("⚠️ ${plato.alergenos}", color = Color(0xFFFFCC80), fontSize = 11.sp) }
            }
            Spacer(Modifier.width(10.dp))
            Column(horizontalAlignment = Alignment.End) {
                Surface(color = ACCENT.copy(0.15f), shape = RoundedCornerShape(8.dp)) {
                    Text("${plato.precio}€", color = ACCENT, fontWeight = FontWeight.Bold, fontSize = 15.sp, modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp))
                }
                Spacer(Modifier.height(6.dp))
                IconButton(onClick = { showConfirm = true }, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.DeleteOutline, "Eliminar", tint = Color(0xFFEF5350), modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AñadirPlatoSheet(
    restauranteId: Int,
    categorias: List<MenuCategoria>,
    onDismiss: () -> Unit,
    onSuccess: () -> Unit,
    onCategoriaCreada: (MenuCategoria) -> Unit
) {
    val scope       = rememberCoroutineScope()
    var nombre      by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var precio      by remember { mutableStateOf("") }
    var alergenos   by remember { mutableStateOf("") }
    var categoriaId  by remember { mutableStateOf(0) }
    var categoriaNom by remember { mutableStateOf("Selecciona categoría") }
    var expandedCat  by remember { mutableStateOf(false) }
    var loading      by remember { mutableStateOf(false) }
    var error        by remember { mutableStateOf<String?>(null) }

    // Estado local de categorías para reflejar las recién creadas sin cerrar el sheet
    var categoriasLocales by remember(categorias) { mutableStateOf(categorias) }

    // Dialog de nueva categoría
    var showNuevaCat     by remember { mutableStateOf(false) }
    var nuevaCatNombre   by remember { mutableStateOf("") }
    var nuevaCatLoading  by remember { mutableStateOf(false) }
    var nuevaCatError    by remember { mutableStateOf<String?>(null) }

    if (showNuevaCat) {
        AlertDialog(
            onDismissRequest = { showNuevaCat = false; nuevaCatNombre = ""; nuevaCatError = null },
            containerColor = Color(0xFF1E1A14),
            title = { Text("Nueva categoría", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(
                        value = nuevaCatNombre,
                        onValueChange = { nuevaCatNombre = it; nuevaCatError = null },
                        label = { Text("Nombre de la categoría") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = fieldColors()
                    )
                    nuevaCatError?.let {
                        Spacer(Modifier.height(8.dp))
                        Text(it, color = Color(0xFFEF5350), fontSize = 12.sp)
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (nuevaCatNombre.isBlank()) { nuevaCatError = "El nombre es obligatorio"; return@TextButton }
                        nuevaCatLoading = true
                        scope.launch {
                            try {
                                val resp = RetrofitClient.instancia.crearCategoria(mapOf(
                                    "restaurante_id" to restauranteId.toString(),
                                    "nombre"         to nuevaCatNombre.trim()
                                ))
                                if (resp.success) {
                                    val nueva = MenuCategoria(id = resp.id, nombre = resp.nombre, orden = resp.orden)
                                    categoriasLocales = categoriasLocales + nueva
                                    onCategoriaCreada(nueva)
                                    categoriaId  = nueva.id
                                    categoriaNom = nueva.nombre
                                    showNuevaCat = false
                                    nuevaCatNombre = ""
                                } else {
                                    nuevaCatError = resp.message.ifBlank { "Error al crear la categoría" }
                                }
                            } catch (_: Exception) {
                                nuevaCatError = "Error de conexión"
                            } finally { nuevaCatLoading = false }
                        }
                    },
                    enabled = !nuevaCatLoading
                ) {
                    if (nuevaCatLoading) CircularProgressIndicator(modifier = Modifier.size(16.dp), color = ACCENT, strokeWidth = 2.dp)
                    else Text("Crear", color = ACCENT, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showNuevaCat = false; nuevaCatNombre = ""; nuevaCatError = null }) {
                    Text("Cancelar", color = Color.Gray)
                }
            }
        )
    }

    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = Color(0xFF1A1408), dragHandle = { BottomSheetDefaults.DragHandle(color = ACCENT.copy(0.5f)) }) {
        Column(modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp).padding(bottom = 32.dp)) {
            Text("Añadir plato al menú", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Spacer(Modifier.height(20.dp))
            OutlinedTextField(value = nombre, onValueChange = { nombre = it; error = null }, label = { Text("Nombre del plato *") }, modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(12.dp), colors = fieldColors())
            Spacer(Modifier.height(12.dp))
            ExposedDropdownMenuBox(expanded = expandedCat, onExpandedChange = { expandedCat = it }) {
                OutlinedTextField(value = categoriaNom, onValueChange = {}, readOnly = true, label = { Text("Categoría *") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCat) }, modifier = Modifier.fillMaxWidth().menuAnchor(), shape = RoundedCornerShape(12.dp), colors = fieldColors())
                ExposedDropdownMenu(expanded = expandedCat, onDismissRequest = { expandedCat = false }, containerColor = Color(0xFF1E1A14)) {
                    categoriasLocales.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat.nombre, color = Color.White) },
                            onClick = { categoriaId = cat.id; categoriaNom = cat.nombre; expandedCat = false }
                        )
                    }
                    HorizontalDivider(color = Color(0xFF3A2E1A))
                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Add, null, tint = ACCENT, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Nueva categoría", color = ACCENT, fontWeight = FontWeight.Medium)
                            }
                        },
                        onClick = { expandedCat = false; showNuevaCat = true }
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(value = descripcion, onValueChange = { descripcion = it }, label = { Text("Descripción (opcional)") }, maxLines = 3, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = fieldColors())
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(value = precio, onValueChange = { precio = it; error = null }, label = { Text("Precio (€) *") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = fieldColors())
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(value = alergenos, onValueChange = { alergenos = it }, label = { Text("Alérgenos (opcional)") }, singleLine = true, placeholder = { Text("gluten, lactosa...", color = Color.Gray) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = fieldColors())
            error?.let {
                Spacer(Modifier.height(10.dp))
                Card(shape = RoundedCornerShape(10.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF2E1A1A))) {
                    Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Warning, null, tint = Color(0xFFEF5350), modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(it, color = Color(0xFFEF5350), fontSize = 13.sp)
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = {
                    when {
                        nombre.isBlank()  -> { error = "El nombre es obligatorio"; return@Button }
                        categoriaId == 0  -> { error = "Selecciona una categoría"; return@Button }
                        precio.toDoubleOrNull() == null || (precio.toDoubleOrNull() ?: 0.0) <= 0.0 -> { error = "Introduce un precio válido"; return@Button }
                    }
                    loading = true
                    scope.launch {
                        try {
                            val resp = RetrofitClient.instancia.crearPlato(mapOf("restaurante_id" to restauranteId.toString(), "categoria_id" to categoriaId.toString(), "nombre" to nombre, "descripcion" to descripcion, "precio" to precio, "alergenos" to alergenos))
                            if (resp.success) onSuccess() else error = resp.message
                        } catch (e: Exception) { error = "Error de conexión" } finally { loading = false }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ACCENT), enabled = !loading
            ) {
                if (loading) CircularProgressIndicator(modifier = Modifier.size(22.dp), color = Color.Black, strokeWidth = 2.dp)
                else { Icon(Icons.Default.Add, null, tint = Color.Black, modifier = Modifier.size(20.dp)); Spacer(Modifier.width(8.dp)); Text("Añadir plato", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp) }
            }
        }
    }
}

@Composable
private fun fieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = ACCENT, unfocusedBorderColor = Color(0xFF3A2E1A),
    focusedLabelColor = ACCENT, unfocusedLabelColor = Color.Gray,
    focusedTextColor = Color.White, unfocusedTextColor = Color.White,
    cursorColor = ACCENT, focusedContainerColor = Color(0xFF1E1A14),
    unfocusedContainerColor = Color(0xFF1E1A14)
)

@Composable
private fun StatCard(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color, modifier: Modifier) {
    Card(modifier = modifier, shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = CARD_BG)) {
        Column(modifier = Modifier.padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, null, tint = color, modifier = Modifier.size(24.dp))
            Spacer(Modifier.height(6.dp))
            Text(value, color = color, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp)
            Text(label, color = Color.Gray, fontSize = 11.sp)
        }
    }
}

@Composable
private fun ReservaRestauranteCard(reserva: Reserva, onConfirmar: () -> Unit, onCancelar: () -> Unit) {
    val (statusColor, statusIcon) = when (reserva.estado) {
        "confirmada"        -> Color(0xFF43A047) to Icons.Default.CheckCircle
        "cancelada"         -> Color(0xFFE53935) to Icons.Default.Cancel
        "completada"        -> Color(0xFF1E88E5) to Icons.Default.TaskAlt
        "esperando_usuario" -> Color(0xFF29B6F6) to Icons.Default.MarkEmailUnread
        else                -> Color(0xFFFFA726) to Icons.Default.HourglassTop
    }
    Card(shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = CARD_BG)) {
        Column(Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(40.dp).background(statusColor.copy(0.2f), CircleShape), Alignment.Center) {
                        Icon(statusIcon, null, tint = statusColor, modifier = Modifier.size(20.dp))
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(reserva.nombreUsuario.ifBlank { "Cliente #${reserva.usuarioId}" }, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        val etiqueta = when (reserva.estado) {
                            "esperando_usuario" -> "Esperando usuario"
                            "confirmada"        -> "Confirmada"
                            "cancelada"         -> "Cancelada"
                            "completada"        -> "Completada"
                            else                -> "Pendiente"
                        }
                        Text(etiqueta, color = statusColor, fontSize = 12.sp)
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(reserva.fecha, color = Color.LightGray, fontSize = 13.sp)
                    Text(reserva.hora, color = ACCENT, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.People, null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("${reserva.numPersonas} personas", color = Color.Gray, fontSize = 13.sp)
                }
                Text("ID #${reserva.id}", color = Color.DarkGray, fontSize = 12.sp)
            }
            if (reserva.notas.isNotBlank()) { Spacer(Modifier.height(6.dp)); Text("📝 ${reserva.notas}", color = Color.LightGray, fontSize = 12.sp) }
            if (reserva.estado == "pendiente") {
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(onClick = onCancelar, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp), border = BorderStroke(1.dp, Color(0xFFEF5350)), colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF5350))) { Text("Cancelar") }
                    Button(onClick = onConfirmar, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF43A047))) { Text("Confirmar", color = Color.White) }
                }
            }
        }
    }
}
