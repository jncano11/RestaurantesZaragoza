package com.example.restauranteszaragoza

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
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
    var screen             by remember { mutableStateOf("login") }
    var selectedRestaurante by remember { mutableStateOf<Restaurante?>(null) }

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
                    onLogout = { SessionManager.cerrarSesion(); screen = "login" },
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
            onLogout = { SessionManager.cerrarSesion(); screen = "login" }
        )

        // ── ROL: admin ────────────────────────────────────────────────────────
        "admin_dashboard" -> AdminDashboardScreen(
            onLogout = { SessionManager.cerrarSesion(); screen = "login" }
        )
    }
}
