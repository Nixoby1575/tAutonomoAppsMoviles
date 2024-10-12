package com.example.taskiapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth

class ConfiguracionActivity : ComponentActivity() {
    // Clave para SharedPreferences
    private val PREFS_NAME = "app_preferences"
    private val THEME_KEY = "theme_is_dark"
    private val NOTIFICATIONS_KEY = "notifications_enabled"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPrefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        val initialThemeIsDark = sharedPrefs.getBoolean(THEME_KEY, false)
        val initialNotificationsEnabled = sharedPrefs.getBoolean(NOTIFICATIONS_KEY, true)

        setContent {
            var themeIsDark by remember { mutableStateOf(initialThemeIsDark) }
            var notificationsEnabled by remember { mutableStateOf(initialNotificationsEnabled) }

            ConfiguracionScreen(
                onLogoutClick = {
                    // Lógica para cerrar sesión
                    FirebaseAuth.getInstance().signOut()

                    // Cambiar valor de isLoggedIn a false en SharedPreferences
                    sharedPrefs.edit().putBoolean("isLoggedIn", false).apply()

                    // Redirigir a MainActivity (pantalla principal)
                    val intent = Intent(this, Login::class.java) // Cambia aquí para apuntar a MainActivity
                    startActivity(intent)
                    finish() // Cierra esta actividad
                },
                themeIsDark = themeIsDark,
                onThemeChange = { isDark ->
                    themeIsDark = isDark
                    savePreference(sharedPrefs, THEME_KEY, isDark)
                },
                notificationsEnabled = notificationsEnabled,
                onNotificationChange = { isEnabled ->
                    notificationsEnabled = isEnabled
                    savePreference(sharedPrefs, NOTIFICATIONS_KEY, isEnabled)
                }
            )
        }
    }

    @Composable
    fun ConfiguracionScreen(
        onLogoutClick: () -> Unit,
        onThemeChange: (Boolean) -> Unit,
        themeIsDark: Boolean,
        notificationsEnabled: Boolean,
        onNotificationChange: (Boolean) -> Unit
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(text = "Configuración", style = MaterialTheme.typography.headlineMedium)

            Spacer(modifier = Modifier.height(16.dp))

            // Opción para activar/desactivar el tema oscuro
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Modo oscuro")
                Switch(
                    checked = themeIsDark,
                    onCheckedChange = onThemeChange // Lógica para cambiar el tema
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Opción para activar/desactivar notificaciones
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Notificaciones")
                Switch(
                    checked = notificationsEnabled,
                    onCheckedChange = onNotificationChange // Lógica para cambiar notificaciones
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón para cerrar sesión
            Button(
                onClick = onLogoutClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error // Color rojo para resaltar
                )
            ) {
                Text(text = "Cerrar sesión", color = Color.White)
            }
        }
    }

    private fun savePreference(sharedPrefs: SharedPreferences, key: String, value: Boolean) {
        sharedPrefs.edit().putBoolean(key, value).apply()
    }
}
