package com.example.restauranteszaragoza.ui.home

import android.content.Context
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.restauranteszaragoza.R
import com.example.restauranteszaragoza.model.Reserva
import com.example.restauranteszaragoza.model.Restaurante
import com.example.restauranteszaragoza.network.RetrofitClient
import com.example.restauranteszaragoza.network.SessionManager
import kotlinx.coroutines.launch

private val BG_TOP  = Color(0xFF0F2027)
private val BG_MID  = Color(0xFF203A43)
private val BG_BOT  = Color(0xFF2C5364)
private val ACCENT  = Color(0xFF00E5FF)
private val CARD_BG = Color(0xFF1A2733)

// ── Categorías disponibles ────────────────────────────────────────────────────
private val CATEGORIAS = listOf(
    "Todas", "Parrilla", "Sushi", "Pasta", "Alta cocina",
    "Mariscos", "Mexicana", "Vegana", "Americana", "Fusión", "Sidreria", "Kebab"
)

// ── Opciones de ordenación ────────────────────────────────────────────────────
private enum class OrdenOpcion(val label: String) {
    NINGUNO("Relevancia"),
    RATING_DESC("Mejor valorados"),
    PRECIO_ASC("Precio: menor a mayor"),
    PRECIO_DESC("Precio: mayor a menor"),
    NOMBRE_ASC("Nombre A-Z")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onRestauranteClick: (Restaurante) -> Unit,
    onLogout: () -> Unit,
    onPerfil: () -> Unit
) {
    val scope    = rememberCoroutineScope()
    val context  = LocalContext.current
    val prefs    = remember { context.getSharedPreferences("restaurantes_prefs", Context.MODE_PRIVATE) }
    var restaurantes          by remember { mutableStateOf<List<Restaurante>>(emptyList()) }
    var misReservas           by remember { mutableStateOf<List<Reserva>>(emptyList()) }
    var loading               by remember { mutableStateOf(true) }
    var searchQuery           by remember { mutableStateOf("") }
    var categoriaSeleccionada by remember { mutableStateOf("Todas") }
    var tabSeleccionado       by remember { mutableStateOf(0) }
    var ordenSeleccionado     by remember { mutableStateOf(OrdenOpcion.NINGUNO) }
    var mostrarOrden          by remember { mutableStateOf(false) }
    var favoritos             by remember {
        mutableStateOf<Set<Int>>(
            prefs.getStringSet("favoritos", emptySet())!!.mapNotNull { it.toIntOrNull() }.toSet()
        )
    }

    LaunchedEffect(favoritos) {
        prefs.edit().putStringSet("favoritos", favoritos.map { it.toString() }.toSet()).apply()
    }

    val usuario = SessionManager.usuarioActual

    LaunchedEffect(restaurantes) {
        if (restaurantes.isNotEmpty()) {
            val idsValidos = restaurantes.map { it.id }.toSet()
            favoritos = favoritos.intersect(idsValidos)
        }
    }

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                restaurantes = RetrofitClient.instancia.listarRestaurantes()
                misReservas  = RetrofitClient.instancia.misReservas(SessionManager.usuarioId)
            } catch (e: Exception) {
                android.util.Log.e("HomeScreen", "Error cargando datos", e)
            } finally {
                loading = false
            }
        }
    }

    // ── Filtrado + ordenación ─────────────────────────────────────────────────
    val restaurantesFiltrados = restaurantes
        .filter { r ->
            val matchBusqueda  = r.nombre.contains(searchQuery, true) ||
                    r.categoria.contains(searchQuery, true) ||
                    r.direccion.contains(searchQuery, true)
            val matchCategoria = categoriaSeleccionada == "Todas" ||
                    r.categoria.contains(categoriaSeleccionada, true)
            matchBusqueda && matchCategoria
        }
        .let { lista ->
            when (ordenSeleccionado) {
                OrdenOpcion.RATING_DESC  -> lista.sortedByDescending { it.ratingGlobal }
                OrdenOpcion.PRECIO_ASC   -> lista.sortedBy { it.precioMedio.replace("[^0-9]".toRegex(), "").toIntOrNull() ?: 0 }
                OrdenOpcion.PRECIO_DESC  -> lista.sortedByDescending { it.precioMedio.replace("[^0-9]".toRegex(), "").toIntOrNull() ?: 0 }
                OrdenOpcion.NOMBRE_ASC   -> lista.sortedBy { it.nombre }
                else                     -> lista
            }
        }

    Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(BG_TOP, BG_MID, BG_BOT)))) {
        Column(modifier = Modifier.fillMaxSize()) {

            // ── Top Bar ──────────────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("¡Hola, ${usuario?.nombre ?: "Foodie"}! 👋", color = Color.LightGray, fontSize = 13.sp)
                    Text("¿Dónde comemos hoy?", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(
                        onClick = onPerfil,
                        modifier = Modifier.background(CARD_BG, CircleShape).size(42.dp)
                    ) {
                        Icon(Icons.Default.AccountCircle, "Mi perfil", tint = ACCENT, modifier = Modifier.size(20.dp))
                    }
                    IconButton(
                        onClick = onLogout,
                        modifier = Modifier.background(CARD_BG, CircleShape).size(42.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Logout, "Cerrar sesión", tint = ACCENT, modifier = Modifier.size(20.dp))
                    }
                }
            }

            // ── Tabs ──────────────────────────────────────────────────────────
            TabRow(
                selectedTabIndex = tabSeleccionado,
                containerColor = Color.Transparent,
                contentColor = ACCENT,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Tab(selected = tabSeleccionado == 0, onClick = { tabSeleccionado = 0 }) {
                    Text("Restaurantes", modifier = Modifier.padding(12.dp),
                        color = if (tabSeleccionado == 0) ACCENT else Color.Gray)
                }
                Tab(selected = tabSeleccionado == 1, onClick = { tabSeleccionado = 1 }) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(12.dp)) {
                        Text("Favoritos", color = if (tabSeleccionado == 1) ACCENT else Color.Gray)
                        if (favoritos.isNotEmpty()) {
                            Spacer(Modifier.width(6.dp))
                            Surface(color = ACCENT, shape = CircleShape, modifier = Modifier.size(18.dp)) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(favoritos.size.toString(), color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
                Tab(selected = tabSeleccionado == 2, onClick = { tabSeleccionado = 2 }) {
                    Text("Mis Reservas", modifier = Modifier.padding(12.dp),
                        color = if (tabSeleccionado == 2) ACCENT else Color.Gray)
                }
            }

            when (tabSeleccionado) {
                // ─────────────── TAB RESTAURANTES ────────────────────────────
                0 -> {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // ── Barra de búsqueda ────────────────────────────────
                        item {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                OutlinedTextField(
                                    value = searchQuery,
                                    onValueChange = { searchQuery = it },
                                    placeholder = { Text("Buscar por nombre, categoría, zona...", color = Color.Gray) },
                                    modifier = Modifier.weight(1f).padding(vertical = 8.dp),
                                    shape = RoundedCornerShape(18.dp),
                                    singleLine = true,
                                    leadingIcon = {
                                        Icon(Icons.Default.Search, null, tint = ACCENT, modifier = Modifier.size(18.dp))
                                    },
                                    trailingIcon = {
                                        if (searchQuery.isNotEmpty()) {
                                            IconButton(onClick = { searchQuery = "" }) {
                                                Icon(Icons.Default.Close, null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                                            }
                                        }
                                    },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor   = CARD_BG,
                                        unfocusedContainerColor = CARD_BG,
                                        focusedTextColor        = Color.White,
                                        unfocusedTextColor      = Color.White,
                                        focusedBorderColor      = ACCENT,
                                        unfocusedBorderColor    = Color(0xFF2A3F4F)
                                    )
                                )
                                Spacer(Modifier.width(8.dp))
                                // Botón ordenar
                                Box {
                                    IconButton(
                                        onClick = { mostrarOrden = true },
                                        modifier = Modifier
                                            .background(
                                                if (ordenSeleccionado != OrdenOpcion.NINGUNO) ACCENT.copy(0.2f) else CARD_BG,
                                                RoundedCornerShape(12.dp)
                                            )
                                            .size(48.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Sort, "Ordenar",
                                            tint = if (ordenSeleccionado != OrdenOpcion.NINGUNO) ACCENT else Color.Gray,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    DropdownMenu(
                                        expanded = mostrarOrden,
                                        onDismissRequest = { mostrarOrden = false },
                                        containerColor = Color(0xFF1A2733)
                                    ) {
                                        Text(
                                            "Ordenar por", color = Color.Gray, fontSize = 11.sp,
                                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                                        )
                                        OrdenOpcion.entries.forEach { opcion ->
                                            DropdownMenuItem(
                                                text = {
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        if (ordenSeleccionado == opcion) {
                                                            Icon(Icons.Default.Check, null, tint = ACCENT, modifier = Modifier.size(14.dp))
                                                            Spacer(Modifier.width(6.dp))
                                                        } else Spacer(Modifier.width(20.dp))
                                                        Text(opcion.label, color = Color.White, fontSize = 14.sp)
                                                    }
                                                },
                                                onClick = { ordenSeleccionado = opcion; mostrarOrden = false }
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // ── Chips de categoría ───────────────────────────────
                        item {
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                contentPadding = PaddingValues(vertical = 4.dp)
                            ) {
                                items(CATEGORIAS) { cat ->
                                    val seleccionado = categoriaSeleccionada == cat
                                    FilterChip(
                                        selected = seleccionado,
                                        onClick  = { categoriaSeleccionada = cat },
                                        label    = { Text(cat, fontSize = 12.sp) },
                                        leadingIcon = if (cat == "Todas") ({
                                            Icon(Icons.Default.GridView, null, modifier = Modifier.size(14.dp))
                                        }) else null,
                                        shape = RoundedCornerShape(20.dp),
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor    = ACCENT,
                                            selectedLabelColor        = Color.Black,
                                            selectedLeadingIconColor  = Color.Black,
                                            containerColor            = CARD_BG,
                                            labelColor                = Color.LightGray
                                        ),
                                        border = FilterChipDefaults.filterChipBorder(
                                            enabled         = true,
                                            selected        = seleccionado,
                                            selectedBorderColor = Color.Transparent,
                                            borderColor         = Color(0xFF2A3F4F)
                                        )
                                    )
                                }
                            }
                        }

                        // ── Contador de resultados + indicador de filtro activo ──
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "${restaurantesFiltrados.size} restaurante${if (restaurantesFiltrados.size != 1) "s" else ""}",
                                    color = Color.Gray, fontSize = 12.sp
                                )
                                // Chip activo de filtro
                                val filtrosActivos = buildList {
                                    if (categoriaSeleccionada != "Todas") add(categoriaSeleccionada)
                                    if (ordenSeleccionado != OrdenOpcion.NINGUNO) add(ordenSeleccionado.label)
                                }
                                if (filtrosActivos.isNotEmpty()) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .background(ACCENT.copy(0.1f), RoundedCornerShape(12.dp))
                                            .clickable {
                                                categoriaSeleccionada = "Todas"
                                                ordenSeleccionado = OrdenOpcion.NINGUNO
                                                searchQuery = ""
                                            }
                                            .padding(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Icon(Icons.Default.FilterAltOff, null, tint = ACCENT, modifier = Modifier.size(12.dp))
                                        Spacer(Modifier.width(4.dp))
                                        Text("Limpiar filtros", color = ACCENT, fontSize = 11.sp)
                                    }
                                }
                            }
                        }

                        // ── Lista de restaurantes ────────────────────────────
                        if (loading) {
                            item {
                                Box(Modifier.fillMaxWidth().padding(48.dp), Alignment.Center) {
                                    CircularProgressIndicator(color = ACCENT)
                                }
                            }
                        } else if (restaurantesFiltrados.isEmpty()) {
                            item {
                                Column(
                                    Modifier.fillMaxWidth().padding(48.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(Icons.Default.SearchOff, null, tint = Color.Gray, modifier = Modifier.size(48.dp))
                                    Spacer(Modifier.height(12.dp))
                                    Text("Sin resultados", color = Color.Gray, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                    Text("Prueba con otro nombre o categoría", color = Color.DarkGray, fontSize = 13.sp)
                                }
                            }
                        } else {
                            items(restaurantesFiltrados) { r ->
                                RestauranteCard(
                                    restaurante = r,
                                    esFavorito  = r.id in favoritos,
                                    onFavoritoToggle = {
                                        favoritos = if (r.id in favoritos)
                                            favoritos - r.id
                                        else
                                            favoritos + r.id
                                    },
                                    onClick = { onRestauranteClick(r) }
                                )
                            }
                        }
                    }
                }

                // ─────────────── TAB FAVORITOS ───────────────────────────────
                1 -> {
                    val favoritosList = restaurantes.filter { it.id in favoritos }
                    if (favoritosList.isEmpty()) {
                        Box(Modifier.fillMaxSize(), Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
                                Icon(Icons.Default.FavoriteBorder, null, tint = Color.Gray, modifier = Modifier.size(64.dp))
                                Spacer(Modifier.height(16.dp))
                                Text("Aún no tienes favoritos", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                Spacer(Modifier.height(8.dp))
                                Text("Pulsa el ❤️ en cualquier restaurante para guardarlo aquí.", color = Color.Gray, fontSize = 14.sp)
                            }
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            item {
                                Text(
                                    "${favoritosList.size} favorito${if (favoritosList.size != 1) "s" else ""}",
                                    color = Color.Gray, fontSize = 12.sp,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                            }
                            items(favoritosList) { r ->
                                RestauranteCard(
                                    restaurante = r,
                                    esFavorito  = true,
                                    onFavoritoToggle = { favoritos = favoritos - r.id },
                                    onClick = { onRestauranteClick(r) }
                                )
                            }
                        }
                    }
                }

                // ─────────────── TAB MIS RESERVAS ────────────────────────────
                2 -> {
                    MisReservasTab(
                        reservas = misReservas,
                        onCancelar = { reservaId ->
                            scope.launch {
                                try {
                                    RetrofitClient.instancia.cancelarReserva(mapOf("reserva_id" to reservaId.toString()))
                                    misReservas = RetrofitClient.instancia.misReservas(SessionManager.usuarioId)
                                } catch (_: Exception) {}
                            }
                        }
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Card de restaurante (con botón favorito)
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun RestauranteCard(
    restaurante: Restaurante,
    esFavorito: Boolean,
    onFavoritoToggle: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape    = RoundedCornerShape(20.dp),
        colors   = CardDefaults.cardColors(containerColor = CARD_BG)
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().height(160.dp)) {
                Image(
                    painter = painterResource(id = imageParaRestaurante(restaurante.categoria)),
                    contentDescription = restaurante.nombre,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                // Badge rating
                if (restaurante.ratingGlobal > 0.0) {
                    Surface(
                        color = Color.Black.copy(alpha = 0.65f),
                        shape = RoundedCornerShape(bottomStart = 10.dp),
                        modifier = Modifier.align(Alignment.TopEnd)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(Icons.Default.Star, null, tint = Color(0xFFFFB300), modifier = Modifier.size(12.dp))
                            Spacer(Modifier.width(3.dp))
                            Text("${restaurante.ratingGlobal}", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                // Botón favorito
                IconButton(
                    onClick = onFavoritoToggle,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(6.dp)
                        .background(Color.Black.copy(0.5f), CircleShape)
                        .size(34.dp)
                ) {
                    Icon(
                        imageVector = if (esFavorito) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = if (esFavorito) "Quitar de favoritos" else "Añadir a favoritos",
                        tint = if (esFavorito) Color(0xFFEF5350) else Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            Column(modifier = Modifier.padding(14.dp)) {
                Text(restaurante.nombre, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp)
                Spacer(Modifier.height(4.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(restaurante.categoria.ifBlank { "General" }, color = ACCENT, fontSize = 13.sp)
                    Text(restaurante.precioMedio, color = Color(0xFF66BB6A), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Place, null, tint = Color.Gray, modifier = Modifier.size(12.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(restaurante.direccion, color = Color.Gray, fontSize = 12.sp, maxLines = 1)
                }
                if (restaurante.aforoTotal > 0) {
                    Spacer(Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.People, null, tint = Color.Gray, modifier = Modifier.size(12.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Aforo: ${restaurante.aforoTotal} personas", color = Color.Gray, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Tab de mis reservas (sin cambios)
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun MisReservasTab(reservas: List<Reserva>, onCancelar: (Int) -> Unit) {
    if (reservas.isEmpty()) {
        Box(Modifier.fillMaxSize(), Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.EventBusy, null, tint = Color.Gray, modifier = Modifier.size(64.dp))
                Spacer(Modifier.height(16.dp))
                Text("No tienes reservas aún", color = Color.Gray, fontSize = 16.sp)
                Text("¡Explora restaurantes y haz tu primera reserva!", color = Color.DarkGray, fontSize = 13.sp)
            }
        }
        return
    }
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items(reservas) { reserva ->
            ReservaCard(reserva = reserva, onCancelar = { onCancelar(reserva.id) })
        }
        item { Spacer(Modifier.height(16.dp)) }
    }
}

@Composable
private fun ReservaCard(reserva: Reserva, onCancelar: () -> Unit) {
    val (estadoColor, estadoIcon) = when (reserva.estado) {
        "confirmada" -> Color(0xFF43A047) to Icons.Default.CheckCircle
        "cancelada"  -> Color(0xFFE53935) to Icons.Default.Cancel
        "completada" -> Color(0xFF1E88E5) to Icons.Default.TaskAlt
        else         -> Color(0xFFFFA726) to Icons.Default.HourglassTop
    }
    var showConfirmDialog by remember { mutableStateOf(false) }

    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title   = { Text("Cancelar reserva") },
            text    = { Text("¿Seguro que quieres cancelar la reserva en ${reserva.nombreRestaurante}?") },
            confirmButton = {
                TextButton(onClick = { showConfirmDialog = false; onCancelar() }) {
                    Text("Sí, cancelar", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) { Text("No") }
            }
        )
    }

    Card(shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = CARD_BG)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                Text(
                    reserva.nombreRestaurante.ifBlank { "Restaurante #${reserva.restauranteId}" },
                    fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp,
                    modifier = Modifier.weight(1f)
                )
                Row(
                    modifier = Modifier
                        .background(estadoColor.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(estadoIcon, null, tint = estadoColor, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(reserva.estado.replaceFirstChar { it.uppercase() }, color = estadoColor, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }
            }
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                InfoChip(icon = Icons.Default.CalendarMonth, text = reserva.fecha)
                InfoChip(icon = Icons.Default.AccessTime,    text = reserva.hora)
                InfoChip(icon = Icons.Default.People,        text = "${reserva.numPersonas} personas")
            }
            if (reserva.notas.isNotBlank()) {
                Spacer(Modifier.height(8.dp))
                Text("📝 ${reserva.notas}", color = Color.LightGray, fontSize = 12.sp)
            }
            if (reserva.estado == "pendiente" || reserva.estado == "confirmada") {
                Spacer(Modifier.height(12.dp))
                OutlinedButton(
                    onClick  = { showConfirmDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape    = RoundedCornerShape(12.dp),
                    colors   = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF5350)),
                    border   = BorderStroke(1.dp, Color(0xFFEF5350))
                ) {
                    Icon(Icons.Default.Cancel, null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Cancelar reserva")
                }
            }
        }
    }
}

@Composable
private fun InfoChip(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = ACCENT, modifier = Modifier.size(14.dp))
        Spacer(Modifier.width(4.dp))
        Text(text, color = Color.LightGray, fontSize = 12.sp)
    }
}

fun imageParaRestaurante(categoria: String): Int {
    return when {
        categoria.contains("Parrilla", true) || categoria.contains("española", true) -> R.drawable.parrilla
        categoria.contains("Sushi",    true) || categoria.contains("japonesa", true)  -> R.drawable.sushi
        categoria.contains("Pasta",    true) || categoria.contains("italiana", true)  -> R.drawable.pasta
        categoria.contains("Alta",     true) -> R.drawable.alta_cocina
        categoria.contains("Mariscos",    true) -> R.drawable.marisco
        categoria.contains("Mexicana",      true) -> R.drawable.mexicana
        categoria.contains("Vegana",         true) -> R.drawable.vegana
        categoria.contains("Americana",          true) -> R.drawable.americana
        categoria.contains("Fusión",          true) -> R.drawable.fusion
        categoria.contains("Sidreria",          true) -> R.drawable.sidreria
        categoria.contains("Kebab",          true) -> R.drawable.kebab
        else -> R.drawable.modern_eats
    }
}
