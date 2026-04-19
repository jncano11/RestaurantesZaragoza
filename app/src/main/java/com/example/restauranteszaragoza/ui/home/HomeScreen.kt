package com.example.restauranteszaragoza.ui.home

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onRestauranteClick: (Restaurante) -> Unit,
    onLogout: () -> Unit,
    onPerfil: () -> Unit
) {
    val scope    = rememberCoroutineScope()
    var restaurantes       by remember { mutableStateOf<List<Restaurante>>(emptyList()) }
    var misReservas        by remember { mutableStateOf<List<Reserva>>(emptyList()) }
    var loading            by remember { mutableStateOf(true) }
    var searchQuery        by remember { mutableStateOf("") }
    var categoriaSeleccionada by remember { mutableStateOf<String?>(null) }
    var tabSeleccionado    by remember { mutableStateOf(0) }

    val usuario = SessionManager.usuarioActual

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

    val restaurantesFiltrados = restaurantes.filter {
        val matchBusqueda  = it.nombre.contains(searchQuery, true) || it.categoria.contains(searchQuery, true)
        val matchCategoria = categoriaSeleccionada == null || it.categoria == categoriaSeleccionada
        matchBusqueda && matchCategoria
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
                    Text("Mis Reservas", modifier = Modifier.padding(12.dp),
                        color = if (tabSeleccionado == 1) ACCENT else Color.Gray)
                }
            }

            if (tabSeleccionado == 0) {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Buscar restaurantes...", color = Color.Gray) },
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            shape = RoundedCornerShape(18.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor   = CARD_BG,
                                unfocusedContainerColor = CARD_BG,
                                focusedTextColor        = Color.White,
                                unfocusedTextColor      = Color.White
                            )
                        )
                    }
                    if (loading) {
                        item {
                            Box(Modifier.fillMaxWidth().padding(48.dp), Alignment.Center) {
                                CircularProgressIndicator(color = ACCENT)
                            }
                        }
                    } else if (restaurantesFiltrados.isEmpty()) {
                        item {
                            Box(Modifier.fillMaxWidth().padding(48.dp), Alignment.Center) {
                                Text("Sin restaurantes disponibles", color = Color.Gray)
                            }
                        }
                    } else {
                        items(restaurantesFiltrados) { r ->
                            RestauranteCard(restaurante = r, onClick = { onRestauranteClick(r) })
                        }
                    }
                }
            } else {
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

// ─────────────────────────────────────────────────────────────────────────────
// Card de restaurante
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun RestauranteCard(restaurante: Restaurante, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape    = RoundedCornerShape(20.dp),
        colors   = CardDefaults.cardColors(containerColor = CARD_BG)
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().height(160.dp)) {
                Image(
                    painter = painterResource(id = imageParaRestaurante(restaurante.categoria)),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                // Badge de rating si está disponible
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
            }
            Column(modifier = Modifier.padding(14.dp)) {
                Text(restaurante.nombre, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp)
                Spacer(Modifier.height(4.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(restaurante.categoria.ifBlank { "General" }, color = ACCENT, fontSize = 13.sp)
                    // precioMedio ya viene como String "15€" desde la API
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
// Tab de mis reservas
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

// ─────────────────────────────────────────────────────────────────────────────
// Card de reserva
// ─────────────────────────────────────────────────────────────────────────────
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

    Card(
        shape  = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = CARD_BG)
    ) {
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
                    Text(
                        reserva.estado.replaceFirstChar { it.uppercase() },
                        color = estadoColor, fontSize = 12.sp, fontWeight = FontWeight.Medium
                    )
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
        categoria.contains("Alta",     true)                                           -> R.drawable.alta_cocina
        else -> R.drawable.modern_eats
    }
}
