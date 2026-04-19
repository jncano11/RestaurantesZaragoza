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
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.restauranteszaragoza.model.Reserva
import com.example.restauranteszaragoza.model.Restaurante
import com.example.restauranteszaragoza.network.RetrofitClient
import com.example.restauranteszaragoza.network.SessionManager
import kotlinx.coroutines.launch

private val ACCENT    = Color(0xFFFFA726)
private val CARD_BG   = Color(0xFF1E1A14)
private val BG_COLORS = listOf(Color(0xFF1A0A00), Color(0xFF2C1810), Color(0xFF1A0A00))

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestauranteDashboardScreen(onLogout: () -> Unit) {
    val scope         = rememberCoroutineScope()
    var reservas      by remember { mutableStateOf<List<Reserva>>(emptyList()) }
    var miRestaurante by remember { mutableStateOf<Restaurante?>(null) }
    var loading       by remember { mutableStateOf(true) }
    var errorMsg      by remember { mutableStateOf<String?>(null) }
    var tabIndex      by remember { mutableStateOf(0) }
    var snackMsg      by remember { mutableStateOf<String?>(null) }
    val snackState    = remember { SnackbarHostState() }

    val usuario = SessionManager.usuarioActual

    fun cargarDatos() {
        loading = true; errorMsg = null
        scope.launch {
            try {
                val r = RetrofitClient.instancia.miRestaurante(SessionManager.usuarioId)
                miRestaurante = r
                reservas = RetrofitClient.instancia.reservasPorRestaurante(r.id)
            } catch (e: Exception) {
                errorMsg = "No se pudo cargar tu restaurante.\nComprueba la conexión."
                android.util.Log.e("RestauranteDashboard", "Error", e)
            } finally { loading = false }
        }
    }

    LaunchedEffect(Unit) { cargarDatos() }

    LaunchedEffect(snackMsg) {
        snackMsg?.let { snackState.showSnackbar(it); snackMsg = null }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackState) },
        containerColor = Color(0xFF1A0A00)
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(BG_COLORS))
                .padding(padding)
        ) {
            Column(Modifier.fillMaxSize()) {

                // ── Header ──────────────────────────────────────────────────
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Brush.horizontalGradient(listOf(Color(0xFF7B3F00), Color(0xFFBF360C))))
                        .padding(20.dp)
                ) {
                    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                        Column {
                            Text("Panel de Restaurante 🍽️", color = Color.White.copy(0.7f), fontSize = 13.sp)
                            Text(
                                "¡Hola, ${usuario?.nombre ?: "Chef"}!",
                                color = Color.White, fontWeight = FontWeight.Bold, fontSize = 22.sp
                            )
                            miRestaurante?.let {
                                Text(it.nombre, color = ACCENT, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                        IconButton(
                            onClick = onLogout,
                            modifier = Modifier.background(Color.Black.copy(0.3f), CircleShape).size(42.dp)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Logout, "Salir", tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                    }
                }

                when {
                    loading -> {
                        Box(Modifier.fillMaxSize(), Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(color = ACCENT)
                                Spacer(Modifier.height(12.dp))
                                Text("Cargando tu restaurante...", color = Color.Gray)
                            }
                        }
                    }

                    errorMsg != null -> {
                        Box(Modifier.fillMaxSize(), Alignment.Center) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(32.dp)
                            ) {
                                Icon(Icons.Default.ErrorOutline, null, tint = Color(0xFFEF5350), modifier = Modifier.size(64.dp))
                                Spacer(Modifier.height(16.dp))
                                Text(errorMsg ?: "", color = Color.LightGray, fontSize = 15.sp, textAlign = TextAlign.Center)
                                Spacer(Modifier.height(20.dp))
                                Button(
                                    onClick = { cargarDatos() },
                                    colors = ButtonDefaults.buttonColors(containerColor = ACCENT),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Default.Refresh, null, tint = Color.Black)
                                    Spacer(Modifier.width(8.dp))
                                    Text("Reintentar", color = Color.Black, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    else -> {
                        // ── Tarjeta info restaurante ─────────────────────────
                        miRestaurante?.let { rest ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = CARD_BG)
                            ) {
                                Column(Modifier.padding(16.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Restaurant, null, tint = ACCENT, modifier = Modifier.size(20.dp))
                                        Spacer(Modifier.width(8.dp))
                                        Text(rest.nombre, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    }
                                    Spacer(Modifier.height(4.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Place, null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                                        Spacer(Modifier.width(4.dp))
                                        Text(rest.direccion, color = Color.Gray, fontSize = 13.sp)
                                    }
                                    Spacer(Modifier.height(2.dp))
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.Category, null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                                            Spacer(Modifier.width(4.dp))
                                            Text(rest.categoria, color = Color.Gray, fontSize = 13.sp)
                                        }
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.EuroSymbol, null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                                            Text(rest.precioMedio, color = Color.Gray, fontSize = 13.sp)
                                        }
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.Star, null, tint = Color(0xFFFFB300), modifier = Modifier.size(14.dp))
                                            Text("${rest.ratingGlobal} (${rest.numValoraciones})", color = Color.Gray, fontSize = 13.sp)
                                        }
                                    }
                                }
                            }
                        }

                        // ── Stats ────────────────────────────────────────────
                        val pendientes  = reservas.count { it.estado == "pendiente" }
                        val confirmadas = reservas.count { it.estado == "confirmada" }
                        val hoy         = reservas.count { it.fecha == java.time.LocalDate.now().toString() }

                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).padding(bottom = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            StatCard("Pendientes",  pendientes.toString(),  Icons.Default.HourglassTop, Color(0xFFFFA726), Modifier.weight(1f))
                            StatCard("Confirmadas", confirmadas.toString(), Icons.Default.CheckCircle,  Color(0xFF43A047), Modifier.weight(1f))
                            StatCard("Hoy",         hoy.toString(),         Icons.Default.Today,        Color(0xFF1E88E5), Modifier.weight(1f))
                        }

                        // ── Tabs ─────────────────────────────────────────────
                        TabRow(selectedTabIndex = tabIndex, containerColor = Color.Transparent, contentColor = ACCENT) {
                            listOf("Todas", "Pendientes", "Hoy").forEachIndexed { idx, label ->
                                Tab(selected = tabIndex == idx, onClick = { tabIndex = idx }) {
                                    Text(label, modifier = Modifier.padding(12.dp),
                                        color = if (tabIndex == idx) ACCENT else Color.Gray)
                                }
                            }
                        }

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
        }
    }
}

@Composable
private fun StatCard(
    label: String, value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color, modifier: Modifier
) {
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
        "confirmada" -> Color(0xFF43A047) to Icons.Default.CheckCircle
        "cancelada"  -> Color(0xFFE53935) to Icons.Default.Cancel
        "completada" -> Color(0xFF1E88E5) to Icons.Default.TaskAlt
        else         -> Color(0xFFFFA726) to Icons.Default.HourglassTop
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
                        Text(reserva.nombreUsuario.ifBlank { "Cliente #${reserva.usuarioId}" },
                            color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text(reserva.estado.replaceFirstChar { it.uppercase() }, color = statusColor, fontSize = 12.sp)
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(reserva.fecha, color = Color.LightGray, fontSize = 13.sp)
                    Text(reserva.hora, color = ACCENT, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.People, null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("${reserva.numPersonas} personas", color = Color.Gray, fontSize = 13.sp)
                }
                Text("ID #${reserva.id}", color = Color.DarkGray, fontSize = 12.sp)
            }
            if (reserva.notas.isNotBlank()) {
                Spacer(Modifier.height(6.dp))
                Text("📝 ${reserva.notas}", color = Color.LightGray, fontSize = 12.sp)
            }
            if (reserva.estado == "pendiente") {
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(
                        onClick = onCancelar, modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFFEF5350)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF5350))
                    ) { Text("Cancelar") }
                    Button(
                        onClick = onConfirmar, modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF43A047))
                    ) { Text("Confirmar", color = Color.White) }
                }
            }
        }
    }
}
