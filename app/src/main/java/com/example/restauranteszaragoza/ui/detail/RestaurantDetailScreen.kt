package com.example.restauranteszaragoza.ui.detail

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.restauranteszaragoza.R
import com.example.restauranteszaragoza.model.Horario
import com.example.restauranteszaragoza.model.PlatoMenu
import com.example.restauranteszaragoza.model.Restaurante
import com.example.restauranteszaragoza.model.Valoracion
import com.example.restauranteszaragoza.network.RetrofitClient
import com.example.restauranteszaragoza.network.SessionManager
import com.example.restauranteszaragoza.ui.home.imageParaRestaurante
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

private val ACCENT  = Color(0xFF00E5FF)
private val CARD_BG = Color(0xFF1A2733)

// ─────────────────────────────────────────────────────────────────────────────
// Pantalla principal de detalle del restaurante
// ─────────────────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantDetailScreen(restaurante: Restaurante, onBack: () -> Unit) {
    val scope   = rememberCoroutineScope()
    val context = LocalContext.current

    var restauranteDetalle   by remember { mutableStateOf<Restaurante?>(null) }
    var valoraciones         by remember { mutableStateOf<List<Valoracion>>(emptyList()) }
    var showReservaSheet     by remember { mutableStateOf(false) }
    var showValoracionDialog by remember { mutableStateOf(false) }
    var snackMsg             by remember { mutableStateOf<String?>(null) }
    val snackState           = remember { SnackbarHostState() }

    LaunchedEffect(restaurante.id) {
        try {
            restauranteDetalle = RetrofitClient.instancia.detalleRestaurante(restaurante.id)
            valoraciones = RetrofitClient.instancia.valoracionesRestaurante(restaurante.id)
        } catch (_: Exception) {
            restauranteDetalle = restaurante
        }
    }

    LaunchedEffect(snackMsg) {
        snackMsg?.let { snackState.showSnackbar(it); snackMsg = null }
    }

    val rest = restauranteDetalle ?: restaurante

    Scaffold(
        snackbarHost = { SnackbarHost(snackState) },
        topBar = {
            TopAppBar(
                title  = { Text(rest.nombre, fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Atrás", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0x990F2027))
            )
        },
        containerColor = Color(0xFF0F2027)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = padding.calculateBottomPadding())
        ) {

            // ── Hero ──────────────────────────────────────────────────────────
            Box(Modifier.fillMaxWidth().height(280.dp)) {
                Image(
                    painter = painterResource(id = imageParaRestaurante(rest.categoria)),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(Modifier.fillMaxSize().background(
                    Brush.verticalGradient(listOf(Color.Transparent, Color(0xFF0F2027)))
                ))
            }

            Column(modifier = Modifier.padding(horizontal = 20.dp).offset(y = (-20).dp)) {

                // ── Info Card ─────────────────────────────────────────────────
                Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = CARD_BG)) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                            Surface(color = ACCENT.copy(alpha = 0.15f), shape = RoundedCornerShape(8.dp)) {
                                Text(
                                    rest.categoria.ifBlank { "General" },
                                    color = ACCENT, fontSize = 12.sp, fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                            if (rest.ratingGlobal > 0) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Star, null, tint = Color(0xFFFFB300), modifier = Modifier.size(18.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text("${rest.ratingGlobal}", color = Color.White, fontWeight = FontWeight.Bold)
                                    Text(" (${rest.numValoraciones})", color = Color.Gray, fontSize = 13.sp)
                                }
                            }
                        }
                        Spacer(Modifier.height(10.dp))
                        Text(rest.nombre, color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp)
                        Spacer(Modifier.height(12.dp))
                        InfoRow(Icons.Default.Place, rest.direccion)
                        if (rest.telefono.isNotBlank()) InfoRow(Icons.Default.Phone, rest.telefono)
                        if (!rest.emailContacto.isNullOrBlank()) InfoRow(Icons.Default.Email, rest.emailContacto)
                        InfoRow(Icons.Default.EuroSymbol, "Precio medio: ${rest.precioMedio}")
                        if (rest.aforoTotal > 0) InfoRow(Icons.Default.People, "Aforo: ${rest.aforoTotal} personas")
                    }
                }

                Spacer(Modifier.height(20.dp))

                // ── Mapa ──────────────────────────────────────────────────────
                if (rest.latitud != 0.0 || rest.longitud != 0.0) {
                    Text("Ubicación", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(Modifier.height(10.dp))
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = CARD_BG),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.LocationOn, null, tint = ACCENT, modifier = Modifier.size(20.dp))
                                Spacer(Modifier.width(8.dp))
                                Text(rest.direccion, color = Color.LightGray, fontSize = 14.sp, modifier = Modifier.weight(1f))
                            }
                            Spacer(Modifier.height(12.dp))
                            Button(
                                onClick = {
                                    val uri = Uri.parse("geo:${rest.latitud},${rest.longitud}?q=${Uri.encode(rest.nombre)}")
                                    val mapIntent = Intent(Intent.ACTION_VIEW, uri).apply { setPackage("com.google.android.apps.maps") }
                                    if (mapIntent.resolveActivity(context.packageManager) != null) {
                                        context.startActivity(mapIntent)
                                    } else {
                                        val webUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=${rest.latitud},${rest.longitud}")
                                        context.startActivity(Intent(Intent.ACTION_VIEW, webUri))
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = ACCENT)
                            ) {
                                Icon(Icons.Default.Map, null, tint = Color.Black, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("Ver en Google Maps", color = Color.Black, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    Spacer(Modifier.height(20.dp))
                }

                // ── Descripción ───────────────────────────────────────────────
                if (rest.descripcion.isNotBlank()) {
                    Text("Sobre el restaurante", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(Modifier.height(8.dp))
                    Text(rest.descripcion, color = Color.LightGray, lineHeight = 22.sp, fontSize = 14.sp)
                    Spacer(Modifier.height(20.dp))
                }

                // ── Horarios ──────────────────────────────────────────────────
                val horarios = restauranteDetalle?.horarios ?: emptyList()
                if (horarios.isNotEmpty()) {
                    Text("Horarios", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(Modifier.height(10.dp))
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = CARD_BG),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            horarios.forEachIndexed { i, horario ->
                                Row(Modifier.fillMaxWidth().padding(vertical = 5.dp), Arrangement.SpaceBetween) {
                                    Text(horario.nombreDia, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                                    if (horario.cerrado) {
                                        Text("Cerrado", color = Color(0xFFEF5350), fontSize = 14.sp)
                                    } else {
                                        Text("${horario.horaApertura} – ${horario.horaCierre}", color = ACCENT, fontSize = 14.sp)
                                    }
                                }
                                if (i < horarios.lastIndex) {
                                    HorizontalDivider(color = Color(0xFF2E3A45), thickness = 0.5.dp)
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(20.dp))
                }

                // ── MENÚ ──────────────────────────────────────────────────────
                val menu = restauranteDetalle?.menu ?: emptyList()
                if (menu.isNotEmpty()) {
                    Text("Menú", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(Modifier.height(10.dp))

                    val menuPorCategoria = menu.groupBy { it.categoriaNombre }
                    menuPorCategoria.forEach { (categoria, platos) ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 10.dp)
                        ) {
                            HorizontalDivider(modifier = Modifier.weight(1f), color = ACCENT.copy(0.3f))
                            Surface(
                                color = ACCENT.copy(alpha = 0.12f),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.padding(horizontal = 10.dp)
                            ) {
                                Text(
                                    categoria,
                                    color = ACCENT, fontWeight = FontWeight.Bold, fontSize = 13.sp,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                                )
                            }
                            HorizontalDivider(modifier = Modifier.weight(1f), color = ACCENT.copy(0.3f))
                        }

                        platos.forEach { plato ->
                            PlatoCard(plato)
                            Spacer(Modifier.height(8.dp))
                        }
                        Spacer(Modifier.height(12.dp))
                    }
                } else if (restauranteDetalle != null) {
                    // El detalle cargó pero no hay menú configurado
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = CARD_BG),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.MenuBook, null, tint = Color.Gray, modifier = Modifier.size(24.dp))
                            Spacer(Modifier.width(12.dp))
                            Text("Menú no disponible aún", color = Color.Gray, fontSize = 14.sp)
                        }
                    }
                    Spacer(Modifier.height(20.dp))
                }

                // ── Galería ───────────────────────────────────────────────────
                Text("Galería", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(Modifier.height(10.dp))
                val imgs = listOf(R.drawable.parrilla, R.drawable.pasta, R.drawable.sushi, R.drawable.alta_cocina, R.drawable.modern_eats)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(imgs) { img ->
                        Image(
                            painter = painterResource(id = img),
                            contentDescription = null,
                            modifier = Modifier.size(150.dp, 100.dp).clip(RoundedCornerShape(14.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))

                // ── Valoraciones ──────────────────────────────────────────────
                Text("Valoraciones", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(Modifier.height(10.dp))
                if (valoraciones.isEmpty()) {
                    Text("Sin valoraciones aún. ¡Sé el primero!", color = Color.Gray, fontSize = 14.sp)
                } else {
                    valoraciones.take(5).forEach { v ->
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = CARD_BG),
                            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                        ) {
                            Column(Modifier.padding(14.dp)) {
                                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                                    Text(v.nombreUsuario, color = Color.White, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                                    Row { repeat(v.puntuacion) { Icon(Icons.Default.Star, null, tint = Color(0xFFFFB300), modifier = Modifier.size(14.dp)) } }
                                }
                                if (v.comentario.isNotBlank()) {
                                    Spacer(Modifier.height(6.dp))
                                    Text(v.comentario, color = Color.LightGray, fontSize = 13.sp)
                                }
                                Text(v.fecha, color = Color.DarkGray, fontSize = 11.sp)
                            }
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                // ── Botones ───────────────────────────────────────────────────
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(
                        onClick = { showValoracionDialog = true },
                        modifier = Modifier.weight(1f).height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, ACCENT),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ACCENT)
                    ) {
                        Icon(Icons.Default.RateReview, null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Valorar")
                    }
                    Button(
                        onClick = { showReservaSheet = true },
                        modifier = Modifier.weight(1f).height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ACCENT)
                    ) {
                        Icon(Icons.Default.BookOnline, null, tint = Color.Black, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Reservar", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(Modifier.height(32.dp))
            }
        }
    }

    if (showReservaSheet) {
        ReservaBottomSheet(
            restaurante = rest,
            horarios    = restauranteDetalle?.horarios ?: emptyList(),
            onDismiss   = { showReservaSheet = false },
            onSuccess   = { msg -> showReservaSheet = false; snackMsg = msg }
        )
    }

    if (showValoracionDialog) {
        ValoracionDialog(
            restauranteId = rest.id,
            onDismiss = { showValoracionDialog = false },
            onSuccess = { msg ->
                showValoracionDialog = false
                snackMsg = msg
                scope.launch {
                    try { valoraciones = RetrofitClient.instancia.valoracionesRestaurante(rest.id) } catch (_: Exception) {}
                }
            }
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Card de plato del menú
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun PlatoCard(plato: PlatoMenu) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF111E28)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(14.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(plato.nombre, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                if (plato.descripcion.isNotBlank()) {
                    Spacer(Modifier.height(2.dp))
                    Text(plato.descripcion, color = Color.Gray, fontSize = 12.sp, maxLines = 2)
                }
                if (plato.alergenos.isNotBlank()) {
                    Spacer(Modifier.height(4.dp))
                    Text("⚠️ ${plato.alergenos}", color = Color(0xFFFFCC80), fontSize = 11.sp)
                }
            }
            Spacer(Modifier.width(12.dp))
            Surface(color = ACCENT.copy(alpha = 0.15f), shape = RoundedCornerShape(8.dp)) {
                Text(
                    "${plato.precio}€", color = ACCENT, fontWeight = FontWeight.Bold, fontSize = 15.sp,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Bottom Sheet de Reserva — con calendario y horas disponibles
// ─────────────────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReservaBottomSheet(
    restaurante: Restaurante,
    horarios: List<Horario>,
    onDismiss: () -> Unit,
    onSuccess: (String) -> Unit
) {
    val scope  = rememberCoroutineScope()
    var fechaSeleccionada by remember { mutableStateOf<LocalDate?>(null) }
    var horaSeleccionada  by remember { mutableStateOf<String?>(null) }
    var personas          by remember { mutableStateOf(2) }
    var notas             by remember { mutableStateOf("") }
    var loading           by remember { mutableStateOf(false) }
    var errorLocal        by remember { mutableStateOf<String?>(null) }

    // Mes actual del calendario
    var mesActual by remember { mutableStateOf(YearMonth.now()) }

    // Generar horas disponibles a partir de los horarios del restaurante para el día seleccionado
    val horasDisponibles: List<String> = remember(fechaSeleccionada, horarios) {
        if (fechaSeleccionada == null) return@remember emptyList()
        val diaSemana = fechaSeleccionada!!.dayOfWeek.value // 1=Lunes … 7=Domingo
        val horarioDia = horarios.find { it.diaSemana == diaSemana }
        if (horarioDia == null || horarioDia.cerrado) return@remember emptyList()

        // Generar slots cada 30 minutos entre apertura y cierre
        val slots = mutableListOf<String>()
        try {
            var h = horarioDia.horaApertura.split(":").let { it[0].toInt() * 60 + it[1].toInt() }
            val hFin = horarioDia.horaCierre.split(":").let { it[0].toInt() * 60 + it[1].toInt() - 30 }
            while (h <= hFin) {
                slots.add("%02d:%02d".format(h / 60, h % 60))
                h += 30
            }
        } catch (_: Exception) {}
        slots
    }

    // Si no hay horarios configurados, generar horas genéricas 12:00–22:00
    val horasMostrar = if (horarios.isEmpty()) {
        (12..21).flatMap { h -> listOf("%02d:00".format(h), "%02d:30".format(h)) }
    } else {
        horasDisponibles
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1A2733),
        dragHandle = { BottomSheetDefaults.DragHandle(color = ACCENT.copy(0.5f)) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
        ) {
            Text("Hacer reserva", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Text(restaurante.nombre, color = ACCENT, fontSize = 14.sp)
            Spacer(Modifier.height(20.dp))

            // ── Calendario ────────────────────────────────────────────────────
            Text("Selecciona la fecha", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            Spacer(Modifier.height(10.dp))

            Card(shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF111E28))) {
                Column(Modifier.padding(14.dp)) {
                    // Cabecera mes
                    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                        IconButton(onClick = { mesActual = mesActual.minusMonths(1) }) {
                            Icon(Icons.Default.ChevronLeft, null, tint = ACCENT)
                        }
                        Text(
                            mesActual.month.getDisplayName(TextStyle.FULL, Locale("es")) + " " + mesActual.year,
                            color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp
                        )
                        IconButton(onClick = { mesActual = mesActual.plusMonths(1) }) {
                            Icon(Icons.Default.ChevronRight, null, tint = ACCENT)
                        }
                    }

                    // Días de la semana
                    Row(Modifier.fillMaxWidth()) {
                        listOf("L","M","X","J","V","S","D").forEach { dia ->
                            Text(
                                dia, color = ACCENT, fontSize = 12.sp, fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f), textAlign = TextAlign.Center
                            )
                        }
                    }
                    Spacer(Modifier.height(6.dp))

                    // Días del mes
                    val primerDia = mesActual.atDay(1)
                    val desplazamiento = (primerDia.dayOfWeek.value - 1) // 0=Lunes
                    val diasEnMes = mesActual.lengthOfMonth()
                    val hoy = LocalDate.now()

                    val celdas = desplazamiento + diasEnMes
                    val filas = (celdas + 6) / 7

                    for (fila in 0 until filas) {
                        Row(Modifier.fillMaxWidth()) {
                            for (col in 0..6) {
                                val idx = fila * 7 + col
                                val dia = idx - desplazamiento + 1
                                if (dia < 1 || dia > diasEnMes) {
                                    Box(Modifier.weight(1f).aspectRatio(1f))
                                } else {
                                    val fecha = mesActual.atDay(dia)
                                    val esPasado = fecha.isBefore(hoy)
                                    val esSeleccionado = fechaSeleccionada == fecha
                                    // Verificar si ese día el restaurante abre
                                    val diaSem = fecha.dayOfWeek.value
                                    val horarioDia = horarios.find { it.diaSemana == diaSem }
                                    val esCerrado = horarioDia?.cerrado == true

                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .aspectRatio(1f)
                                            .padding(2.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(
                                                when {
                                                    esSeleccionado -> ACCENT
                                                    fecha == hoy   -> ACCENT.copy(0.2f)
                                                    else           -> Color.Transparent
                                                }
                                            )
                                            .then(
                                                if (!esPasado && !esCerrado)
                                                    Modifier.clickable {
                                                        fechaSeleccionada = fecha
                                                        horaSeleccionada = null
                                                        errorLocal = null
                                                    }
                                                else Modifier
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            "$dia",
                                            color = when {
                                                esSeleccionado -> Color.Black
                                                esPasado || esCerrado -> Color.Gray.copy(0.4f)
                                                else -> Color.White
                                            },
                                            fontSize = 13.sp,
                                            fontWeight = if (esSeleccionado) FontWeight.Bold else FontWeight.Normal
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Leyenda días cerrados
                    if (horarios.isNotEmpty()) {
                        Spacer(Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(Modifier.size(10.dp).clip(RoundedCornerShape(3.dp)).background(Color.Gray.copy(0.3f)))
                            Spacer(Modifier.width(6.dp))
                            Text("Días cerrados / pasados", color = Color.Gray, fontSize = 11.sp)
                        }
                    }
                }
            }

            // Fecha seleccionada
            fechaSeleccionada?.let {
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF43A047), modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(
                        "Fecha: ${it.dayOfWeek.getDisplayName(TextStyle.FULL, Locale("es"))} ${it.dayOfMonth}/${it.monthValue}/${it.year}",
                        color = Color(0xFF43A047), fontSize = 13.sp, fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // ── Horas disponibles ─────────────────────────────────────────────
            Text("Selecciona la hora", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            Spacer(Modifier.height(10.dp))

            if (fechaSeleccionada == null) {
                Text("Primero selecciona una fecha", color = Color.Gray, fontSize = 13.sp)
            } else if (horasMostrar.isEmpty()) {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF2E1A1A))
                ) {
                    Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.EventBusy, null, tint = Color(0xFFEF5350), modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(10.dp))
                        Text("El restaurante está cerrado ese día", color = Color(0xFFEF5350), fontSize = 14.sp)
                    }
                }
            } else {
                // Grid de horas en 4 columnas
                val filaHoras = horasMostrar.chunked(4)
                filaHoras.forEach { fila ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        fila.forEach { hora ->
                            val seleccionada = horaSeleccionada == hora
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { horaSeleccionada = hora; errorLocal = null },
                                shape = RoundedCornerShape(10.dp),
                                color = if (seleccionada) ACCENT else Color(0xFF111E28),
                                border = if (!seleccionada) BorderStroke(1.dp, Color(0xFF2E3A45)) else null
                            ) {
                                Text(
                                    hora,
                                    color = if (seleccionada) Color.Black else Color.White,
                                    fontWeight = if (seleccionada) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 13.sp, textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 10.dp)
                                )
                            }
                        }
                        // Relleno si la fila tiene menos de 4
                        repeat(4 - fila.size) {
                            Box(Modifier.weight(1f))
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // ── Número de personas ────────────────────────────────────────────
            Text("Número de personas", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            Spacer(Modifier.height(10.dp))
            Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF111E28))) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { if (personas > 1) { personas--; errorLocal = null } },
                        enabled = personas > 1
                    ) {
                        Icon(
                            Icons.Default.RemoveCircleOutline, null,
                            tint = if (personas > 1) ACCENT else Color.Gray,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("$personas", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 28.sp)
                        Text("personas", color = Color.Gray, fontSize = 12.sp)
                    }
                    IconButton(
                        onClick = { if (personas < 20) { personas++; errorLocal = null } },
                        enabled = personas < 20
                    ) {
                        Icon(
                            Icons.Default.AddCircleOutline, null,
                            tint = if (personas < 20) ACCENT else Color.Gray,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // ── Notas opcionales ──────────────────────────────────────────────
            Text("Notas opcionales", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = notas, onValueChange = { notas = it },
                placeholder = { Text("Alergias, peticiones especiales...", color = Color.Gray) },
                maxLines = 3, modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp), colors = sheetFieldColors()
            )

            // Error
            errorLocal?.let {
                Spacer(Modifier.height(10.dp))
                Card(shape = RoundedCornerShape(10.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF2E1A1A))) {
                    Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Warning, null, tint = Color(0xFFEF5350), modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(it, color = Color(0xFFEF5350), fontSize = 13.sp)
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // ── Botón Confirmar ───────────────────────────────────────────────
            Button(
                onClick = {
                    when {
                        fechaSeleccionada == null -> { errorLocal = "⚠️ Selecciona una fecha en el calendario"; return@Button }
                        horaSeleccionada == null  -> { errorLocal = "⚠️ Selecciona una hora disponible"; return@Button }
                        SessionManager.usuarioId == 0 -> { errorLocal = "❌ Sesión expirada, vuelve a iniciar sesión"; return@Button }
                    }
                    loading = true
                    scope.launch {
                        try {
                            val resp = RetrofitClient.instancia.crearReserva(mapOf(
                                "usuario_id"     to SessionManager.usuarioId.toString(),
                                "restaurante_id" to restaurante.id.toString(),
                                "fecha"          to fechaSeleccionada!!.toString(),           // formato YYYY-MM-DD
                                "hora"           to horaSeleccionada!!,                       // formato HH:MM
                                "num_personas"   to personas.toString(),
                                "notas"          to notas
                            ))
                            onSuccess(if (resp.success) "✅ Reserva realizada con éxito" else "❌ ${resp.message}")
                        } catch (e: Exception) {
                            onSuccess("❌ Error de conexión: ${e.localizedMessage}")
                        } finally { loading = false }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ACCENT),
                enabled = !loading && fechaSeleccionada != null && horaSeleccionada != null
            ) {
                if (loading) {
                    CircularProgressIndicator(modifier = Modifier.size(22.dp), color = Color.Black, strokeWidth = 2.dp)
                } else {
                    Icon(Icons.Default.CheckCircle, null, tint = Color.Black, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Confirmar reserva", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Dialog de Valoración
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun ValoracionDialog(restauranteId: Int, onDismiss: () -> Unit, onSuccess: (String) -> Unit) {
    val scope      = rememberCoroutineScope()
    var puntuacion by remember { mutableStateOf(5) }
    var comentario by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor   = Color(0xFF1A2733),
        title = { Text("Valorar restaurante", color = Color.White, fontWeight = FontWeight.Bold) },
        text  = {
            Column {
                Text("Puntuación:", color = Color.LightGray, fontSize = 14.sp)
                Spacer(Modifier.height(8.dp))
                Row {
                    (1..5).forEach { i ->
                        IconButton(onClick = { puntuacion = i }) {
                            Icon(Icons.Default.Star, null,
                                tint = if (i <= puntuacion) Color(0xFFFFB300) else Color.Gray,
                                modifier = Modifier.size(28.dp))
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = comentario, onValueChange = { comentario = it },
                    label = { Text("Comentario (opcional)") }, maxLines = 4,
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                    colors = sheetFieldColors()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                scope.launch {
                    try {
                        val resp = RetrofitClient.instancia.crearValoracion(mapOf(
                            "usuario_id"     to SessionManager.usuarioId.toString(),
                            "restaurante_id" to restauranteId.toString(),
                            "puntuacion"     to puntuacion.toString(),
                            "comentario"     to comentario
                        ))
                        onSuccess(if (resp.success) "✅ Valoración enviada" else "❌ ${resp.message}")
                    } catch (_: Exception) { onSuccess("❌ Error de conexión") }
                }
            }) { Text("Enviar", color = ACCENT, fontWeight = FontWeight.Bold) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar", color = Color.Gray) } }
    )
}

@Composable
private fun InfoRow(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(modifier = Modifier.padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = ACCENT, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(10.dp))
        Text(text, color = Color.LightGray, fontSize = 14.sp)
    }
}

@Composable
private fun sheetFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor   = ACCENT, unfocusedBorderColor = Color(0xFF2E3A45),
    focusedLabelColor    = ACCENT, unfocusedLabelColor  = Color.Gray,
    focusedTextColor     = Color.White, unfocusedTextColor = Color.White,
    cursorColor          = ACCENT
)
