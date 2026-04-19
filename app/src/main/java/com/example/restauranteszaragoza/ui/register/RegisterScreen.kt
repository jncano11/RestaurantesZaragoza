package com.example.restauranteszaragoza.ui.register

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import com.example.restauranteszaragoza.network.RetrofitClient
import com.example.restauranteszaragoza.ui.login.textFieldColors
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onBackToLogin: () -> Unit
) {
    var nombre    by remember { mutableStateOf("") }
    var apellidos by remember { mutableStateOf("") }
    var email     by remember { mutableStateOf("") }
    var telefono  by remember { mutableStateOf("") }
    var password  by remember { mutableStateOf("") }
    var showPass  by remember { mutableStateOf(false) }
    var errorMsg  by remember { mutableStateOf<String?>(null) }
    var loading   by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF0F2027), Color(0xFF203A43), Color(0xFF2C5364))))
            .padding(24.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth().align(Alignment.Center),
            shape    = RoundedCornerShape(28.dp),
            elevation= CardDefaults.cardElevation(12.dp),
            colors   = CardDefaults.cardColors(containerColor = Color(0xFF121212))
        ) {
            Column(
                modifier = Modifier.padding(28.dp).verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Crear cuenta", style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold, color = Color.White))
                Text("Únete a R-eats Zaragoza", color = Color.LightGray)

                Spacer(Modifier.height(24.dp))

                // Error banner
                errorMsg?.let { msg ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF7F1C1C)),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                    ) {
                        Text(msg, color = Color.White, modifier = Modifier.padding(12.dp))
                    }
                }

                // Campos
                FormField("Nombre *", nombre) { nombre = it; errorMsg = null }
                FormField("Apellidos", apellidos) { apellidos = it }
                FormField("Correo electrónico *", email) { email = it; errorMsg = null }
                FormField("Teléfono", telefono) { telefono = it }

                // Contraseña con toggle de visibilidad
                OutlinedTextField(
                    value           = password,
                    onValueChange   = { password = it; errorMsg = null },
                    label           = { Text("Contraseña * (mín. 6 caracteres)") },
                    modifier        = Modifier.fillMaxWidth(),
                    singleLine      = true,
                    shape           = RoundedCornerShape(16.dp),
                    colors          = textFieldColors(),
                    visualTransformation = if (showPass) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon    = {
                        IconButton(onClick = { showPass = !showPass }) {
                            Icon(if (showPass) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                "Toggle contraseña", tint = Color.Gray)
                        }
                    }
                )

                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (nombre.isBlank() || email.isBlank() || password.isBlank()) {
                            errorMsg = "Los campos marcados con * son obligatorios"
                            return@Button
                        }
                        if (password.length < 6) {
                            errorMsg = "La contraseña debe tener al menos 6 caracteres"
                            return@Button
                        }
                        loading = true
                        scope.launch {
                            try {
                                val resp = RetrofitClient.instancia.registrar(
                                    mapOf(
                                        "nombre"     to nombre,
                                        "apellidos"  to apellidos,
                                        "email"      to email,
                                        "contrasena" to password,
                                        "telefono"   to telefono
                                    )
                                )
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
        value       = value,
        onValueChange = onChange,
        label       = { Text(label) },
        modifier    = Modifier.fillMaxWidth(),
        singleLine  = true,
        shape       = RoundedCornerShape(16.dp),
        colors      = textFieldColors()
    )
    Spacer(Modifier.height(12.dp))
}
