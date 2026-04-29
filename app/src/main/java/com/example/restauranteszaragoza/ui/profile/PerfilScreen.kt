package com.example.restauranteszaragoza.ui.perfil

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import com.example.restauranteszaragoza.model.PerfilStats
import com.example.restauranteszaragoza.model.Reserva
import com.example.restauranteszaragoza.network.RetrofitClient
import com.example.restauranteszaragoza.network.SessionManager
import kotlinx.coroutines.launch

private val ACCENT  = Color(0xFF00E5FF)
private val CARD_BG = Color(0xFF1A2733)
private val BG      = listOf(Color(0xFF0F2027), Color(0xFF203A43), Color(0xFF2C5364))

@Composable
fun PerfilScreen(onBack: () -> Unit) {
    val scope = rememberCoroutineScope()
    var stats   by remember { mutableStateOf<PerfilStats?>(null) }
    var loading by remember { mutableStateOf(true) }
    var error   by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                stats = RetrofitClient.instancia.perfilStats(SessionManager.usuarioId)
            } catch (e: Exception) {
                error = "No se pudo cargar el perfil"
            } finally { loading = false }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(BG))
    ) {
        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {

            // ── Header ────────────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.verticalGradient(listOf(Color(0xFF0F2027), Color.Transparent)))
                    .padding(top = 48.dp, bottom = 24.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Botón volver
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, "Atrás", tint = Color.White)
                        }
                        Text("Mi Perfil", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        // Placeholder para simetría
                        Box(Modifier.size(48.dp))
                    }

                    Spacer(Modifier.height(16.dp))

                    // Avatar
                    val usuario = stats?.usuario ?: SessionManager.usuarioActual
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .background(Brush.linearGradient(listOf(ACCENT, Color(0xFF0080FF))), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            usuario?.nombre?.firstOrNull()?.uppercase() ?: "?",
                            color = Color.Black, fontWeight = FontWeight.ExtraBold, fontSize = 36.sp
                        )
                    }

                    Spacer(Modifier.height(12.dp))
                    Text(
                        "${usuario?.nombre ?: ""} ${usuario?.apellidos ?: ""}".trim(),
                        color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp
                    )
                    Text(usuario?.email ?: "", color = ACCENT, fontSize = 14.sp)
                    Spacer(Modifier.height(6.dp))
                    Surface(color = ACCENT.copy(0.15f), shape = RoundedCornerShape(20.dp)) {
                        Text(
                            (usuario?.rol ?: "usuario").uppercase(),
                            color = ACCENT, fontSize = 12.sp, fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 5.dp)
                        )
                    }
                }
            }

            // ── Contenido ─────────────────────────────────────────────────────
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {

                when {
                    loading -> {
                        Box(Modifier.fillMaxWidth().padding(48.dp), Alignment.Center) {
                            CircularProgressIndicator(color = ACCENT)
                        }
                    }

                    error != null -> {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF2E1A1A)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.ErrorOutline, null, tint = Color(0xFFEF5350), modifier = Modifier.size(24.dp))
                                Spacer(Modifier.width(12.dp))
                                Text(error ?: "", color = Color(0xFFEF5350))
                            }
                        }
                    }

                    else -> {
                        val s = stats!!

                        // ── Datos de contacto ─────────────────────────────────
                        Text("Información personal", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(Modifier.height(10.dp))
                        Card(
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = CARD_BG),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(Modifier.padding(16.dp)) {
                                val usuario = s.usuario
                                DatoRow(Icons.Default.Person, "Nombre", "${usuario?.nombre} ${usuario?.apellidos}")
                                HorizontalDivider(color = Color(0xFF2E3A45), thickness = 0.5.dp, modifier = Modifier.padding(vertical = 8.dp))
                                DatoRow(Icons.Default.Email, "Email", usuario?.email ?: "")
                                if (!usuario?.telefono.isNullOrBlank()) {
                                    HorizontalDivider(color = Color(0xFF2E3A45), thickness = 0.5.dp, modifier = Modifier.padding(vertical = 8.dp))
                                    DatoRow(Icons.Default.Phone, "Teléfono", usuario?.telefono ?: "")
                                }
                            }
                        }

                        Spacer(Modifier.height(24.dp))

                        // ── Estadísticas generales ────────────────────────────
                        Text("Mis estadísticas", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(Modifier.height(10.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            StatCard(
                                icon = Icons.Default.BookOnline,
                                valor = s.totalReservas.toString(),
                                label = "Reservas",
                                color = ACCENT,
                                modifier = Modifier.weight(1f)
                            )
                            StatCard(
                                icon = Icons.Default.Restaurant,
                                valor = s.restaurantesVisitados.toString(),
                                label = "Restaurantes",
                                color = Color(0xFFFFA726),
                                modifier = Modifier.weight(1f)
                            )
                            StatCard(
                                icon = Icons.Default.People,
                                valor = s.totalPersonas.toString(),
                                label = "Comensales",
                                color = Color(0xFF66BB6A),
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(Modifier.height(10.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            StatCard(
                                icon = Icons.Default.Star,
                                valor = "${s.totalValoraciones}",
                                label = "Valoraciones",
                                color = Color(0xFFFFB300),
                                modifier = Modifier.weight(1f)
                            )
                            StatCard(
                                icon = Icons.Default.StarHalf,
                                valor = "${s.mediaPuntuacion}",
                                label = "Media dada",
                                color = Color(0xFFFFB300),
                                modifier = Modifier.weight(1f)
                            )
                            StatCard(
                                icon = Icons.Default.CheckCircle,
                                valor = s.confirmadas.toString(),
                                label = "Confirmadas",
                                color = Color(0xFF43A047),
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(Modifier.height(24.dp))

                        // ── Desglose de reservas ──────────────────────────────
                        Text("Estado de mis reservas", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(Modifier.height(10.dp))
                        Card(
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = CARD_BG),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(Modifier.padding(16.dp)) {
                                EstadoRow("Pendientes",  s.pendientes,  Color(0xFFFFA726), Icons.Default.HourglassTop)
                                HorizontalDivider(color = Color(0xFF2E3A45), thickness = 0.5.dp, modifier = Modifier.padding(vertical = 8.dp))
                                EstadoRow("Confirmadas", s.confirmadas, Color(0xFF43A047), Icons.Default.CheckCircle)
                                HorizontalDivider(color = Color(0xFF2E3A45), thickness = 0.5.dp, modifier = Modifier.padding(vertical = 8.dp))
                                EstadoRow("Completadas", s.completadas, Color(0xFF1E88E5), Icons.Default.TaskAlt)
                                HorizontalDivider(color = Color(0xFF2E3A45), thickness = 0.5.dp, modifier = Modifier.padding(vertical = 8.dp))
                                EstadoRow("Canceladas",  s.canceladas,  Color(0xFFEF5350), Icons.Default.Cancel)
                            }
                        }

                        Spacer(Modifier.height(24.dp))

                        // ── Últimas reservas ──────────────────────────────────
                        if (s.ultimasReservas.isNotEmpty()) {
                            Text("Últimas reservas", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Spacer(Modifier.height(10.dp))
                            s.ultimasReservas.forEach { reserva ->
                                UltimaReservaCard(reserva)
                                Spacer(Modifier.height(8.dp))
                            }
                        }

                        Spacer(Modifier.height(32.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun DatoRow(icon: ImageVector, label: String, valor: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = ACCENT, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(12.dp))
        Column {
            Text(label, color = Color.Gray, fontSize = 11.sp)
            Text(valor, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun StatCard(icon: ImageVector, valor: String, label: String, color: Color, modifier: Modifier) {
    Card(modifier = modifier, shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = CARD_BG)) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, null, tint = color, modifier = Modifier.size(22.dp))
            Spacer(Modifier.height(6.dp))
            Text(valor, color = color, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
            Text(label, color = Color.Gray, fontSize = 10.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        }
    }
}

@Composable
private fun EstadoRow(label: String, count: Int, color: Color, icon: ImageVector) {
    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = color, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(10.dp))
            Text(label, color = Color.LightGray, fontSize = 14.sp)
        }
        Surface(color = color.copy(0.15f), shape = RoundedCornerShape(8.dp)) {
            Text(
                "$count", color = color, fontWeight = FontWeight.Bold, fontSize = 14.sp,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
            )
        }
    }
}

@Composable
private fun UltimaReservaCard(reserva: Reserva) {
    val (color, icon) = when (reserva.estado) {
        "confirmada" -> Color(0xFF43A047) to Icons.Default.CheckCircle
        "cancelada"  -> Color(0xFFEF5350) to Icons.Default.Cancel
        "completada" -> Color(0xFF1E88E5) to Icons.Default.TaskAlt
        else         -> Color(0xFFFFA726) to Icons.Default.HourglassTop
    }
    Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = CARD_BG), modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(40.dp).background(color.copy(0.15f), CircleShape), Alignment.Center) {
                Icon(icon, null, tint = color, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(reserva.nombreRestaurante, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("${reserva.fecha} · ${reserva.hora} · ${reserva.numPersonas} pax", color = Color.Gray, fontSize = 12.sp)
            }
            Text(reserva.estado.replaceFirstChar { it.uppercase() }, color = color, fontSize = 12.sp, fontWeight = FontWeight.Medium)
        }
    }
}
