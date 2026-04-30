package com.example.restauranteszaragoza.ui.login

import android.content.Context
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.restauranteszaragoza.R
import com.example.restauranteszaragoza.model.LoginResponse
import com.example.restauranteszaragoza.network.RetrofitClient
import com.example.restauranteszaragoza.network.SessionManager
import com.google.gson.Gson
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onRegisterClick: () -> Unit
) {
    val context   = LocalContext.current
    val prefs     = remember { context.getSharedPreferences("restaurantes_prefs", Context.MODE_PRIVATE) }
    var email     by remember { mutableStateOf(prefs.getString("saved_email", "") ?: "") }
    var password  by remember { mutableStateOf(prefs.getString("saved_password", "") ?: "") }
    var recuerdame by remember { mutableStateOf(prefs.getBoolean("recuerdame", false)) }
    var errorMsg  by remember { mutableStateOf<String?>(null) }
    var loading   by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // Auto-login si Recuérdame estaba activo
    LaunchedEffect(Unit) {
        if (recuerdame && email.isNotBlank() && password.isNotBlank()) {
            loading = true
            try {
                val httpResp = RetrofitClient.instancia.login(
                    mapOf("email" to email, "contrasena" to password)
                )
                val resp = if (httpResp.isSuccessful) httpResp.body() else
                    httpResp.errorBody()?.string()?.let { Gson().fromJson(it, LoginResponse::class.java) }
                if (resp?.success == true && resp.usuario != null) {
                    SessionManager.usuarioActual = resp.usuario
                    onLoginSuccess()
                }
            } catch (_: Exception) {
            } finally {
                loading = false
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF0F2027), Color(0xFF203A43), Color(0xFF2C5364))
                )
            )
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            elevation = CardDefaults.cardElevation(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF121212))
        ) {
            Column(
                modifier = Modifier.padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.reats),
                    contentDescription = "Logo",
                    modifier = Modifier.size(120.dp).padding(bottom = 12.dp),
                    contentScale = ContentScale.Fit
                )
                Text(
                    "R-eats Zaragoza",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold, color = Color.White
                    )
                )
                Text("Explora lo mejor de la ciudad", color = Color.LightGray)

                Spacer(Modifier.height(28.dp))

                // Error banner
                AnimatedVisibility(visible = errorMsg != null) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF7F1C1C)),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                    ) {
                        Text(
                            errorMsg ?: "",
                            color = Color.White,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it; errorMsg = null },
                    label = { Text("Correo electrónico") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = textFieldColors()
                )
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it; errorMsg = null },
                    label = { Text("Contraseña") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    shape = RoundedCornerShape(16.dp),
                    colors = textFieldColors()
                )

                Spacer(Modifier.height(12.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = recuerdame,
                        onCheckedChange = { recuerdame = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = Color(0xFF00E5FF),
                            uncheckedColor = Color.Gray,
                            checkmarkColor = Color.Black
                        )
                    )
                    Text("Recuérdame", color = Color.LightGray)
                }

                Spacer(Modifier.height(12.dp))

                Button(
                    onClick = {
                        if (email.isBlank() || password.isBlank()) {
                            errorMsg = "Completa todos los campos"
                            return@Button
                        }
                        loading = true
                        scope.launch {
                            try {
                                val httpResp = RetrofitClient.instancia.login(
                                    mapOf("email" to email, "contrasena" to password)
                                )
                                val resp = if (httpResp.isSuccessful) httpResp.body() else
                                    httpResp.errorBody()?.string()?.let { Gson().fromJson(it, LoginResponse::class.java) }
                                if (resp?.success == true && resp.usuario != null) {
                                    SessionManager.usuarioActual = resp.usuario
                                    if (recuerdame) {
                                        prefs.edit()
                                            .putBoolean("recuerdame", true)
                                            .putString("saved_email", email)
                                            .putString("saved_password", password)
                                            .apply()
                                    } else {
                                        prefs.edit()
                                            .remove("recuerdame")
                                            .remove("saved_email")
                                            .remove("saved_password")
                                            .apply()
                                    }
                                    onLoginSuccess()
                                } else {
                                    errorMsg = resp?.message ?: "Credenciales incorrectas"
                                }
                            } catch (e: Exception) {
                                errorMsg = "Error de conexión. Comprueba el servidor."
                            } finally {
                                loading = false
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF)),
                    enabled = !loading
                ) {
                    if (loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.Black,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Entrar", color = Color.Black, style = MaterialTheme.typography.titleMedium)
                    }
                }

                Spacer(Modifier.height(14.dp))
                TextButton(onClick = onRegisterClick) {
                    Text("¿No tienes cuenta? Regístrate", color = Color(0xFF00E5FF))
                }
            }
        }
    }
}

@Composable
fun textFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor   = Color(0xFF00E5FF),
    focusedLabelColor    = Color(0xFF00E5FF),
    unfocusedBorderColor = Color.Gray,
    unfocusedLabelColor  = Color.Gray,
    cursorColor          = Color(0xFF00E5FF),
    focusedTextColor     = Color.White,
    unfocusedTextColor   = Color.White
)
