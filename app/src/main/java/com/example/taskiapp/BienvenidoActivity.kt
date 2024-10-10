package com.example.taskiapp
import androidx.compose.ui.Modifier
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext

class BienvenidoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Obtener el nombre del usuario desde Shared Preferences
        val sharedPref = getSharedPreferences("MyAppPreferences", MODE_PRIVATE)
        val userName = sharedPref.getString("userName", "Usuario") // Valor por defecto

        setContent {
            BienvenidoScreen(userName = userName ?: "Usuario", onLogout = {
                // Lógica para cerrar sesión
                sharedPref.edit().clear().apply() // Limpiar todas las preferencias

                // Navegar a MainActivity
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish() // Cierra esta actividad
            })
        }
    }
}

@Composable
fun BienvenidoScreen(userName: String, onLogout: () -> Unit) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Cuadro verde con mensaje de bienvenida
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF4CAF50)) // Color verde
                .padding(16.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "BIENVENIDO", color = Color.White)
                Text(text = userName, color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Botón de cerrar sesión
        Button(onClick = onLogout) {
            Text(text = "Cerrar sesión")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón para ir a la actividad de agregar nota
        Button(onClick = {
            val intent = Intent(context, NotaActivity::class.java)
            context.startActivity(intent)
        }) {
            Text(text = "Agregar Nota")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón para ir a la actividad de mis notas
        Button(onClick = {
            val intent = Intent(context, MisNotasActivity::class.java)
            context.startActivity(intent)
        }) {
            Text(text = "Mis Notas")
        }

        // Puedes agregar más botones para "Archivados", "Perfil", "Acerca de", etc.
    }
}
