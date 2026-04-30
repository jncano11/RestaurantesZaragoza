package com.example.restauranteszaragoza.ui.register

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.restauranteszaragoza.network.RetrofitClient
import com.example.restauranteszaragoza.ui.login.textFieldColors
import kotlinx.coroutines.launch

private val CATEGORIAS_RESTAURANTE = listOf(
    "Parrilla", "Sushi", "Pasta", "Alta cocina",
    "Mariscos", "Mexicana", "Vegana", "Americana", "Fusión", "Sidreria", "Kebab"
)

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onBackToLogin: () -> Unit
) {
    // ── Datos usuario ──────────────────────────────────────────
    var nombre    by remember { mutableStateOf("") }
    var apellidos by remember { mutableStateOf("") }
    var email     by remember { mutableStateOf("") }
    var telefono  by remember { mutableStateOf("") }
    var password  by remember { mutableStateOf("") }
    var showPass  by remember { mutableStateOf(false) }

    // ── Modo restaurante ───────────────────────────────────────
    var esRestaurante         by remember { mutableStateOf(false) }
    var nombreRestaurante     by remember { mutableStateOf("") }
    var descripcion           by remember { mutableStateOf("") }
    var direccion             by remember { mutableStateOf("") }
    var categoriaSeleccionada by remember { mutableStateOf("") }
    var telefonoRestaurante   by remember { mutableStateOf("") }
    var dropdownAbierto       by remember { mutableStateOf(false) }

    var errorMsg by remember { mutableStateOf<String?>(null) }
    var loading  by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF0F2027), Color(0xFF203A43), Color(0xFF2C5364))))
            .padding(24.dp)
    ) {
        Card(
            modifier  = Modifier.fillMaxWidth().align(Alignment.Center),
            shape     = RoundedCornerShape(28.dp),
            elevation = CardDefaults.cardElevation(12.dp),
            colors    = CardDefaults.cardColors(containerColor = Color(0xFF121212))
        ) {
            Column(
                modifier = Modifier.padding(28.dp).verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    if (esRestaurante) "Registro de restaurante" else "Crear cuenta",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold, color = Color.White)
                )
                Text("Únete a R-eats Zaragoza", color = Color.LightGray)

                Spacer(Modifier.height(24.dp))

                // Error banner
                errorMsg?.let { msg ->
                    Card(
                        colors   = CardDefaults.cardColors(containerColor = Color(0xFF7F1C1C)),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                    ) {
                        Text(msg, color = Color.White, modifier = Modifier.padding(12.dp))
                    }
                }

                // ── Datos personales ───────────────────────────────────
                FormField("Nombre *", nombre) { nombre = it; errorMsg = null }
                FormField("Apellidos", apellidos) { apellidos = it }
                FormField("Correo electrónico *", email) { email = it; errorMsg = null }
                if (!esRestaurante) {
                    FormField("Teléfono", telefono) { telefono = it }
                }

                OutlinedTextField(
                    value                = password,
                    onValueChange        = { password = it; errorMsg = null },
                    label                = { Text("Contraseña * (mín. 6 caracteres)") },
                    modifier             = Modifier.fillMaxWidth(),
                    singleLine           = true,
                    shape                = RoundedCornerShape(16.dp),
                    colors               = textFieldColors(),
                    visualTransformation = if (showPass) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon         = {
                        IconButton(onClick = { showPass = !showPass }) {
                            Icon(
                                if (showPass) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                "Toggle contraseña", tint = Color.Gray
                            )
                        }
                    }
                )

                Spacer(Modifier.height(20.dp))

                // ── Botón ¿Eres un restaurante? ────────────────────────
                OutlinedButton(
                    onClick = { esRestaurante = !esRestaurante; errorMsg = null },
                    modifier = Modifier.fillMaxWidth(),
                    shape    = RoundedCornerShape(14.dp),
                    colors   = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (esRestaurante) Color(0xFF7C4DFF).copy(0.15f) else Color.Transparent
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.5.dp, if (esRestaurante) Color(0xFF7C4DFF) else Color(0xFF7C4DFF).copy(0.5f)
                    )
                ) {
                    Icon(Icons.Default.Restaurant, null, tint = Color(0xFF7C4DFF), modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        if (esRestaurante) "✓ Registrando como restaurante" else "¿Eres un restaurante?",
                        color = Color(0xFF7C4DFF),
                        fontWeight = if (esRestaurante) FontWeight.Bold else FontWeight.Normal
                    )
                }

                // ── Formulario restaurante (animado) ───────────────────
                AnimatedVisibility(
                    visible = esRestaurante,
                    enter   = expandVertically(),
                    exit    = shrinkVertically()
                ) {
                    Column {
                        Spacer(Modifier.height(16.dp))

                        HorizontalDivider(color = Color(0xFF7C4DFF).copy(0.3f))
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Datos del restaurante",
                            color = Color(0xFF7C4DFF),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        FormField("Nombre del restaurante *", nombreRestaurante) { nombreRestaurante = it; errorMsg = null }
                        FormField("Dirección *", direccion) { direccion = it; errorMsg = null }
                        FormField("Teléfono del restaurante", telefonoRestaurante) { telefonoRestaurante = it }

                        // Dropdown categoría
                        Box(Modifier.fillMaxWidth()) {
                            OutlinedButton(
                                onClick  = { dropdownAbierto = true },
                                modifier = Modifier.fillMaxWidth(),
                                shape    = RoundedCornerShape(16.dp),
                                border   = androidx.compose.foundation.BorderStroke(1.dp, Color.Gray),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp)
                            ) {
                                Text(
                                    categoriaSeleccionada.ifEmpty { "Categoría" },
                                    color    = if (categoriaSeleccionada.isEmpty()) Color.Gray else Color.White,
                                    modifier = Modifier.weight(1f),
                                    fontSize = 14.sp
                                )
                                Icon(Icons.Default.ArrowDropDown, null, tint = Color.Gray)
                            }
                            DropdownMenu(
                                expanded         = dropdownAbierto,
                                onDismissRequest = { dropdownAbierto = false },
                                containerColor   = Color(0xFF1E1B2E),
                                modifier         = Modifier.fillMaxWidth(0.85f)
                            ) {
                                CATEGORIAS_RESTAURANTE.forEach { cat ->
                                    DropdownMenuItem(
                                        text    = { Text(cat, color = Color.White) },
                                        onClick = { categoriaSeleccionada = cat; dropdownAbierto = false }
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(12.dp))

                        OutlinedTextField(
                            value         = descripcion,
                            onValueChange = { descripcion = it },
                            label         = { Text("Descripción") },
                            modifier      = Modifier.fillMaxWidth().height(100.dp),
                            shape         = RoundedCornerShape(16.dp),
                            colors        = textFieldColors(),
                            maxLines      = 4
                        )
                        Spacer(Modifier.height(8.dp))

                        Surface(
                            color  = Color(0xFF7C4DFF).copy(0.08f),
                            shape  = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                "Tu restaurante quedará pendiente de aprobación por un administrador antes de aparecer en la app.",
                                color    = Color(0xFFB39DDB),
                                fontSize = 12.sp,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = {
                        errorMsg = null
                        if (nombre.isBlank() || email.isBlank() || password.isBlank()) {
                            errorMsg = "Los campos marcados con * son obligatorios"; return@Button
                        }
                        if (password.length < 6) {
                            errorMsg = "La contraseña debe tener al menos 6 caracteres"; return@Button
                        }
                        if (esRestaurante && (nombreRestaurante.isBlank() || direccion.isBlank())) {
                            errorMsg = "Nombre y dirección del restaurante son obligatorios"; return@Button
                        }
                        loading = true
                        scope.launch {
                            try {
                                val resp = if (esRestaurante) {
                                    RetrofitClient.instancia.registrarRestaurante(
                                        mapOf(
                                            "nombre"               to nombre,
                                            "apellidos"            to apellidos,
                                            "email"                to email,
                                            "contrasena"           to password,
                                            "telefono"             to telefono,
                                            "nombre_restaurante"   to nombreRestaurante,
                                            "descripcion"          to descripcion,
                                            "direccion"            to direccion,
                                            "categoria"            to categoriaSeleccionada,
                                            "telefono_restaurante" to telefonoRestaurante
                                        )
                                    )
                                } else {
                                    RetrofitClient.instancia.registrar(
                                        mapOf(
                                            "nombre"     to nombre,
                                            "apellidos"  to apellidos,
                                            "email"      to email,
                                            "contrasena" to password,
                                            "telefono"   to telefono
                                        )
                                    )
                                }
                                if (resp.success) {
                                    onRegisterSuccess()
                                } else {
                                    errorMsg = resp.message.ifBlank { "Error al registrar" }
                                }
                            } catch (e: Exception) {
                                errorMsg = "Error de conexión. Comprueba el servidor."
                            } finally {
                                loading = false
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    shape    = RoundedCornerShape(18.dp),
                    colors   = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF)),
                    enabled  = !loading
                ) {
                    if (loading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.Black, strokeWidth = 2.dp)
                    } else {
                        Text("Registrarse", color = Color.Black, style = MaterialTheme.typography.titleMedium)
                    }
                }

                Spacer(Modifier.height(14.dp))
                TextButton(onClick = onBackToLogin) {
                    Text("← Volver al login", color = Color(0xFF00E5FF))
                }
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun FormField(label: String, value: String, onChange: (String) -> Unit) {
    OutlinedTextField(
        value         = value,
        onValueChange = onChange,
        label         = { Text(label) },
        modifier      = Modifier.fillMaxWidth(),
        singleLine    = true,
        shape         = RoundedCornerShape(16.dp),
        colors        = textFieldColors()
    )
    Spacer(Modifier.height(12.dp))
}
