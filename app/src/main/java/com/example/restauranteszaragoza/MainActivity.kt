package com.example.restauranteszaragoza

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.example.restauranteszaragoza.model.Restaurante
import com.example.restauranteszaragoza.network.SessionManager
import com.example.restauranteszaragoza.ui.admin.AdminDashboardScreen
import com.example.restauranteszaragoza.ui.detail.RestaurantDetailScreen
import com.example.restauranteszaragoza.ui.home.HomeScreen
import com.example.restauranteszaragoza.ui.login.LoginScreen
import com.example.restauranteszaragoza.ui.perfil.PerfilScreen
import com.example.restauranteszaragoza.ui.register.RegisterScreen
import com.example.restauranteszaragoza.ui.restaurante.RestauranteDashboardScreen
import com.example.restauranteszaragoza.ui.theme.RestaurantesZaragozaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RestaurantesZaragozaTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val context             = LocalContext.current
    var screen             by remember { mutableStateOf("login") }
    var selectedRestaurante by remember { mutableStateOf<Restaurante?>(null) }

    fun logout() {
        context.getSharedPreferences("restaurantes_prefs", Context.MODE_PRIVATE)
            .edit()
            .remove("recuerdame")
            .remove("saved_email")
            .remove("saved_password")
            .apply()
        SessionManager.cerrarSesion()
        screen = "login"
    }

    when (screen) {

        "login" -> LoginScreen(
            onLoginSuccess = {
                screen = when (SessionManager.rolActual) {
                    "admin"       -> "admin_dashboard"
                    "restaurante" -> "restaurante_dashboard"
                    else          -> "home"
                }
            },
            onRegisterClick = { screen = "register" }
        )

        "register" -> RegisterScreen(
            onRegisterSuccess = { screen = "login" },
            onBackToLogin     = { screen = "login" }
        )

        // ── ROL: usuario ──────────────────────────────────────────────────────
        "home" -> {
            if (selectedRestaurante == null) {
                HomeScreen(
                    onRestauranteClick = { r -> selectedRestaurante = r },
                    onLogout = { logout() },
                    onPerfil = { screen = "perfil" }
                )
            } else {
                RestaurantDetailScreen(
                    restaurante = selectedRestaurante!!,
                    onBack      = { selectedRestaurante = null }
                )
            }
        }

        "perfil" -> PerfilScreen(
            onBack = { screen = "home" }
        )

        // ── ROL: restaurante ──────────────────────────────────────────────────
        "restaurante_dashboard" -> RestauranteDashboardScreen(
            onLogout = { logout() }
        )

        // ── ROL: admin ────────────────────────────────────────────────────────
        "admin_dashboard" -> AdminDashboardScreen(
            onLogout = { logout() }
        )
    }
}
