package com.example.restauranteszaragoza.ui.admin

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.restauranteszaragoza.model.EstadisticasAdmin
import com.example.restauranteszaragoza.model.Reserva
import com.example.restauranteszaragoza.model.Restaurante
import com.example.restauranteszaragoza.model.Usuario
import com.example.restauranteszaragoza.network.RetrofitClient
import com.example.restauranteszaragoza.network.SessionManager
import kotlinx.coroutines.launch

private val ACCENT    = Color(0xFF7C4DFF)
private val CARD_BG   = Color(0xFF13111E)
private val BG_COLORS = listOf(Color(0xFF0A0814), Color(0xFF110F1F), Color(0xFF0A0814))

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(onLogout: () -> Unit) {
    val scope        = rememberCoroutineScope()
    var tabIndex     by remember { mutableStateOf(0) }
    var stats        by remember { mutableStateOf(EstadisticasAdmin()) }
    var usuarios     by remember { mutableStateOf<List<Usuario>>(emptyList()) }
    var restaurantes by remember { mutableStateOf<List<Restaurante>>(emptyList()) }
    var reservas     by remember { mutableStateOf<List<Reserva>>(emptyList()) }
    var snackMsg     by remember { mutableStateOf<String?>(null) }
    val snackState   = remember { SnackbarHostState() }

    fun recargar() {
        scope.launch {
            try {
                stats        = RetrofitClient.instancia.estadisticasAdmin()
                usuarios     = RetrofitClient.instancia.listarUsuarios()
                restaurantes = RetrofitClient.instancia.listarRestaurantesAdmin()
                reservas     = RetrofitClient.instancia.todasReservas()
            } catch (_: Exception) {
                stats = EstadisticasAdmin(totalUsuarios=12, totalRestaurantes=8, totalReservas=47, reservasHoy=5, ingresosPropinas=138.50)
            }
        }
    }

    LaunchedEffect(Unit) { recargar() }
    LaunchedEffect(snackMsg) { snackMsg?.let { snackState.showSnackbar(it); snackMsg = null } }

    Scaffold(snackbarHost = { SnackbarHost(snackState) }, containerColor = Color(0xFF0A0814)) { padding ->
        Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(BG_COLORS)).padding(padding)) {
            Column(Modifier.fillMaxSize()) {

                // ── Header ───────────────────────────────────────────────────
                Box(
                    modifier = Modifier.fillMaxWidth()
                        .background(Brush.horizontalGradient(listOf(Color(0xFF4527A0), Color(0xFF7B1FA2))))
                        .padding(20.dp)
                ) {
                    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AdminPanelSettings, null, tint = Color.White.copy(0.6f), modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Panel de Administración", color = Color.White.copy(0.7f), fontSize = 13.sp)
                            }
                            Text("Bienvenido, ${SessionManager.usuarioActual?.nombre ?: "Admin"}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        }
                        IconButton(onClick = onLogout, modifier = Modifier.background(Color.Black.copy(0.3f), CircleShape).size(42.dp)) {
                            Icon(Icons.AutoMirrored.Filled.Logout, "Salir", tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                    }
                }

                // ── Stats ─────────────────────────────────────────────────────
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxWidth().height(180.dp).padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement   = Arrangement.spacedBy(10.dp)
                ) {
                    item { AdminStatCard("Usuarios",     stats.totalUsuarios.toString(),        Icons.Default.People,     Color(0xFF42A5F5)) }
                    item { AdminStatCard("Restaurantes", stats.totalRestaurantes.toString(),     Icons.Default.Restaurant, Color(0xFFFFA726)) }
                    item { AdminStatCard("Reservas",     stats.totalReservas.toString(),         Icons.Default.EventNote,  Color(0xFF66BB6A)) }
                    item { AdminStatCard("Propinas €",   "%.2f".format(stats.ingresosPropinas), Icons.Default.Payments,   Color(0xFFAB47BC)) }
                }

                // ── Tabs ──────────────────────────────────────────────────────
                val tabs = listOf("Usuarios", "Restaurantes", "Reservas")
                TabRow(selectedTabIndex = tabIndex, containerColor = Color.Transparent, contentColor = ACCENT) {
                    tabs.forEachIndexed { i, label ->
                        Tab(selected = tabIndex == i, onClick = { tabIndex = i }) {
                            Text(label, modifier = Modifier.padding(10.dp), color = if (tabIndex == i) ACCENT else Color.Gray, fontSize = 13.sp)
                        }
                    }
                }

                when (tabIndex) {

                    // ── Usuarios ──────────────────────────────────────────────
                    0 -> LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(usuarios) { u ->
                            AdminUsuarioCard(
                                usuario = u,
                                onToggleActivo = {
                                    scope.launch {
                                        try {
                                            RetrofitClient.instancia.toggleUsuario(mapOf("usuario_id" to u.id.toString()))
                                            usuarios = RetrofitClient.instancia.listarUsuarios()
                                            snackMsg = "Estado actualizado"
                                        } catch (_: Exception) { snackMsg = "❌ Error de conexión" }
                                    }
                                },
                                onCambiarRol = { nuevoRol ->
                                    scope.launch {
                                        try {
                                            RetrofitClient.instancia.cambiarRolUsuario(mapOf(
                                                "usuario_id" to u.id.toString(),
                                                "nuevo_rol"  to nuevoRol
                                            ))
                                            usuarios = RetrofitClient.instancia.listarUsuarios()
                                            snackMsg = "✅ Rol cambiado a $nuevoRol"
                                        } catch (_: Exception) { snackMsg = "❌ Error al cambiar rol" }
                                    }
                                },
                                onEliminar = {
                                    scope.launch {
                                        try {
                                            RetrofitClient.instancia.eliminarUsuario(mapOf("usuario_id" to u.id.toString()))
                                            usuarios = RetrofitClient.instancia.listarUsuarios()
                                            stats    = RetrofitClient.instancia.estadisticasAdmin()
                                            snackMsg = "🗑️ Usuario eliminado"
                                        } catch (_: Exception) { snackMsg = "❌ Error al eliminar" }
                                    }
                                }
                            )
                        }
                        item { Spacer(Modifier.height(24.dp)) }
                    }

                    // ── Restaurantes ──────────────────────────────────────────
                    1 -> LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(restaurantes) { r ->
                            AdminRestauranteCard(restaurante = r, onToggle = {
                                scope.launch {
                                    try {
                                        RetrofitClient.instancia.toggleRestaurante(mapOf("restaurante_id" to r.id.toString()))
                                        restaurantes = RetrofitClient.instancia.listarRestaurantesAdmin()
                                        snackMsg = "Restaurante actualizado"
                                    } catch (_: Exception) { snackMsg = "❌ Error de conexión" }
                                }
                            })
                        }
                        item { Spacer(Modifier.height(24.dp)) }
                    }

                    // ── Reservas ──────────────────────────────────────────────
                    2 -> LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(reservas) { r -> AdminReservaCard(reserva = r) }
                        item { Spacer(Modifier.height(24.dp)) }
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun AdminStatCard(label: String, value: String, icon: ImageVector, color: Color) {
    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = CARD_BG)) {
        Row(modifier = Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(44.dp).background(color.copy(0.15f), CircleShape), Alignment.Center) {
                Icon(icon, null, tint = color, modifier = Modifier.size(22.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text(value, color = color, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                Text(label, color = Color.Gray, fontSize = 12.sp)
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Card de Usuario con: activar/desactivar, cambiar rol, eliminar
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun AdminUsuarioCard(
    usuario: Usuario,
    onToggleActivo: () -> Unit,
    onCambiarRol: (String) -> Unit,
    onEliminar: () -> Unit
) {
    val rolColor = when (usuario.rol) {
        "admin"       -> Color(0xFFAB47BC)
        "restaurante" -> Color(0xFFFFA726)
        else          -> Color(0xFF42A5F5)
    }
    var showRolMenu      by remember { mutableStateOf(false) }
    var showConfirmElim  by remember { mutableStateOf(false) }

    // Diálogo confirmación eliminar
    if (showConfirmElim) {
        AlertDialog(
            onDismissRequest = { showConfirmElim = false },
            containerColor   = Color(0xFF13111E),
            title = { Text("Eliminar usuario", color = Color.White, fontWeight = FontWeight.Bold) },
            text  = { Text("¿Eliminar a ${usuario.nombre} ${usuario.apellidos}?\nSe borrarán sus reservas y valoraciones.", color = Color.LightGray) },
            confirmButton = {
                TextButton(onClick = { showConfirmElim = false; onEliminar() }) {
                    Text("Eliminar", color = Color(0xFFEF5350), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = { TextButton(onClick = { showConfirmElim = false }) { Text("Cancelar", color = Color.Gray) } }
        )
    }

    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = CARD_BG)) {
        Column(Modifier.padding(14.dp)) {
            // Fila principal
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(46.dp).background(rolColor.copy(0.15f), CircleShape), Alignment.Center) {
                    Text(usuario.nombre.firstOrNull()?.uppercase() ?: "?", color = rolColor, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text("${usuario.nombre} ${usuario.apellidos}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(usuario.email, color = Color.Gray, fontSize = 12.sp)
                    Spacer(Modifier.height(4.dp))
                    Surface(color = rolColor.copy(0.15f), shape = RoundedCornerShape(8.dp)) {
                        Text(usuario.rol.uppercase(), color = rolColor, fontSize = 10.sp, fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                    }
                }
            }

            Spacer(Modifier.height(10.dp))
            HorizontalDivider(color = Color(0xFF2A2435), thickness = 0.5.dp)
            Spacer(Modifier.height(10.dp))

            // Fila de acciones
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {

                // Botón activar/desactivar
                OutlinedButton(
                    onClick = onToggleActivo,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Color(0xFF42A5F5).copy(0.5f)),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.ToggleOn, null, tint = Color(0xFF42A5F5), modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Activar/Des", color = Color(0xFF42A5F5), fontSize = 11.sp)
                }

                // Botón cambiar rol (dropdown)
                Box(Modifier.weight(1f)) {
                    OutlinedButton(
                        onClick = { showRolMenu = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, ACCENT.copy(0.5f)),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.ManageAccounts, null, tint = ACCENT, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Rol", color = ACCENT, fontSize = 11.sp)
                        Icon(Icons.Default.ArrowDropDown, null, tint = ACCENT, modifier = Modifier.size(14.dp))
                    }
                    DropdownMenu(
                        expanded = showRolMenu,
                        onDismissRequest = { showRolMenu = false },
                        containerColor = Color(0xFF1E1B2E)
                    ) {
                        listOf("usuario" to Color(0xFF42A5F5), "restaurante" to Color(0xFFFFA726), "admin" to Color(0xFFAB47BC)).forEach { (rol, color) ->
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(Modifier.size(8.dp).background(color, CircleShape))
                                        Spacer(Modifier.width(8.dp))
                                        Text(rol.replaceFirstChar { it.uppercase() }, color = Color.White)
                                        if (rol == usuario.rol) {
                                            Spacer(Modifier.width(8.dp))
                                            Icon(Icons.Default.Check, null, tint = color, modifier = Modifier.size(14.dp))
                                        }
                                    }
                                },
                                onClick = { showRolMenu = false; if (rol != usuario.rol) onCambiarRol(rol) }
                            )
                        }
                    }
                }

                // Botón eliminar
                OutlinedButton(
                    onClick = { showConfirmElim = true },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Color(0xFFEF5350).copy(0.5f)),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.DeleteOutline, null, tint = Color(0xFFEF5350), modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Eliminar", color = Color(0xFFEF5350), fontSize = 11.sp)
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun AdminRestauranteCard(restaurante: Restaurante, onToggle: () -> Unit) {
    val isActivo    = restaurante.activo == 1
    val estadoColor = if (isActivo) Color(0xFF66BB6A) else Color(0xFFEF5350)
    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = CARD_BG)) {
        Row(modifier = Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(46.dp).background(Color(0xFFFFA726).copy(0.15f), CircleShape), Alignment.Center) {
                Icon(Icons.Default.Restaurant, null, tint = Color(0xFFFFA726), modifier = Modifier.size(22.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(restaurante.nombre, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(restaurante.direccion, color = Color.Gray, fontSize = 12.sp, maxLines = 1)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, null, tint = Color(0xFFFFB300), modifier = Modifier.size(12.dp))
                        Text(" ${restaurante.ratingGlobal} · ${restaurante.categoria}", color = Color.LightGray, fontSize = 12.sp)
                    }
                    Surface(color = estadoColor.copy(0.15f), shape = RoundedCornerShape(6.dp)) {
                        Text(if (isActivo) "ACTIVO" else "INACTIVO", color = estadoColor, fontSize = 10.sp,
                            fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                    }
                }
            }
            IconButton(onClick = onToggle) {
                Icon(
                    if (isActivo) Icons.Default.ToggleOn else Icons.Default.ToggleOff,
                    null, tint = if (isActivo) ACCENT else Color.Gray, modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun AdminReservaCard(reserva: Reserva) {
    val (color, icon) = when (reserva.estado) {
        "confirmada" -> Color(0xFF43A047) to Icons.Default.CheckCircle
        "cancelada"  -> Color(0xFFE53935) to Icons.Default.Cancel
        "completada" -> Color(0xFF1E88E5) to Icons.Default.TaskAlt
        else         -> Color(0xFFFFA726) to Icons.Default.HourglassTop
    }
    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = CARD_BG)) {
        Row(modifier = Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = color, modifier = Modifier.size(28.dp))
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(reserva.nombreRestaurante.ifBlank { "Restaurante #${reserva.restauranteId}" }, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("${reserva.nombreUsuario.ifBlank { "Usuario #${reserva.usuarioId}" }} · ${reserva.numPersonas} pax", color = Color.Gray, fontSize = 12.sp)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(reserva.fecha, color = Color.LightGray, fontSize = 12.sp)
                Text(reserva.hora, color = ACCENT, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Text(reserva.estado, color = color, fontSize = 11.sp)
            }
        }
    }
}
