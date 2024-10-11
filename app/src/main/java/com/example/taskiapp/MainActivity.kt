package com.example.taskiapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.taskiapp.ui.theme.TASKIAPPTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPref = getSharedPreferences("MyAppPreferences", MODE_PRIVATE)
        val isLoggedIn = sharedPref.getBoolean("isLoggedIn", false)

        if (isLoggedIn) {
            // Si el usuario ya ha iniciado sesión, redirigir a BienvenidoActivity
            val intent = Intent(this, BienvenidoActivity::class.java)
            startActivity(intent)
            finish() // Cierra la actividad actual para que no pueda regresar al login
        } else {
            // Si no ha iniciado sesión, mostrar la pantalla de inicio
            setContent {
                TASKIAPPTheme {
                    SplashScreen(
                        modifier = Modifier.fillMaxSize(),
                        activity = this
                    )
                }
            }
        }
    }

    @Composable
    fun SplashScreen(modifier: Modifier = Modifier, activity: ComponentActivity) {
        Column(
            modifier = modifier
                .padding(16.dp)
                .background(Color(0xFFF5F5F5)), // Fondo gris claro
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_notas), // Reemplaza con tu logo
                contentDescription = "Logo de la app",
                modifier = Modifier.size(150.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Bienvenido a tu Agenda Online",
                style = MaterialTheme.typography.headlineLarge,
                color = Color(0xFF100E0E) // Cambia a un color que te guste
            )
            Spacer(modifier = Modifier.height(24.dp))

            Button(onClick = {
                val intent = Intent(activity, Login::class.java)
                activity.startActivity(intent)
            }, shape = RoundedCornerShape(10.dp)) { // Bordes redondeados
                Text(text = "Login")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                val intent = Intent(activity, Registro::class.java)
                activity.startActivity(intent)
            }, shape = RoundedCornerShape(10.dp)) {
                Text(text = "Registro")
            }
        }
    }
}
