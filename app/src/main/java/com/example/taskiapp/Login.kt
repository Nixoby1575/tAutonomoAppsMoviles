package com.example.taskiapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.taskiapp.ui.theme.TASKIAPPTheme
import com.google.firebase.auth.FirebaseAuth

class Login : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TASKIAPPTheme {
                LoginScreen()
            }
        }
    }
}

@Composable
fun LoginScreen() {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current // Obtener el contexto
    val sharedPref = context.getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE) // Obtener SharedPreferences

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Iniciar sesión", modifier = Modifier.padding(bottom = 16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo electrónico") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = {
            val auth = FirebaseAuth.getInstance()
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Obtener el nombre del usuario
                        val user = auth.currentUser
                        val userName = user?.displayName ?: "Usuario"

                        // Guardar el estado de inicio de sesión y el nombre de usuario
                        with(sharedPref.edit()) {
                            putBoolean("isLoggedIn", true)
                            putString("userName", userName) // Guardar el nombre de usuario
                            apply()
                        }

                        // Mostrar mensaje de éxito
                        Toast.makeText(context, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()

                        // Navegar a la actividad de bienvenida
                        val intent = Intent(context, BienvenidoActivity::class.java)
                        intent.putExtra("userName", userName)
                        context.startActivity(intent)
                    } else {
                        Toast.makeText(context, "Error en el inicio de sesión", Toast.LENGTH_SHORT).show()
                    }
                }
        }) {
            Text(text = "Login")
        }
    }
}
