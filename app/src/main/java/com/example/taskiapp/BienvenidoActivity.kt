package com.example.taskiapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

class BienvenidoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Obtener el nombre del usuario desde Shared Preferences
        val sharedPref = getSharedPreferences("MyAppPreferences", MODE_PRIVATE)
        val userName = sharedPref.getString("userName", "Usuario") // Valor por defecto

        setContent {
            BienvenidoScreen(userName = userName ?: "Usuario", onLogout = {
                // Lógica para cerrar sesión
                sharedPref.edit().putBoolean("isLoggedIn", false).apply()

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

    // Fondo de pantalla
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White) // Color de fondo
    ) {
        // Encabezado
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(MaterialTheme.colorScheme.primary) // Color del encabezado
                .padding(16.dp)
        ) {
            Text(
                text = "Agenda Online",
                style = MaterialTheme.typography.headlineLarge.copy(color = Color.White),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Bienvenido(a)",
                style = MaterialTheme.typography.bodyMedium.copy(color = Color.White),
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = userName,
                style = MaterialTheme.typography.bodyLarge.copy(color = Color.White)
            )
        }

        // Espaciado
        Spacer(modifier = Modifier.height(32.dp))

        // Cuadro de opciones
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .align(Alignment.Center)
        ) {
            // Fila con dos columnas
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly // Distribuir espacio uniformemente
            ) {
                // Primera columna
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f) // Ocupa el mismo espacio que la segunda columna
                ) {
                    // Imagen para Agregar Nota
                    Image(
                        painter = painterResource(id = R.drawable.agregar_nota),
                        contentDescription = "Agregar Nota",
                        modifier = Modifier
                            .size(80.dp) // Tamaño de la imagen
                            .clickable {
                                val intent = Intent(context, NotaActivity::class.java)
                                context.startActivity(intent)
                            },
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    // Botón de Agregar Nota
                    Button(
                        onClick = {
                            val intent = Intent(context, NotaActivity::class.java)
                            context.startActivity(intent)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                        modifier = Modifier.fillMaxWidth(0.8f)
                    ) {
                        Text(text = "Agregar Nota", color = Color.White)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Imagen para Mis Notas
                    Image(
                        painter = painterResource(id = R.drawable.mis_notas),
                        contentDescription = "Mis Notas",
                        modifier = Modifier
                            .size(80.dp) // Tamaño de la imagen
                            .clickable {
                                val intent = Intent(context, MisNotasActivity::class.java)
                                context.startActivity(intent)
                            },
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    // Botón de Mis Notas
                    Button(
                        onClick = {
                            val intent = Intent(context, MisNotasActivity::class.java)
                            context.startActivity(intent)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                        modifier = Modifier.fillMaxWidth(0.8f)
                    ) {
                        Text(text = "Mis Notas", color = Color.White)
                    }
                }

                // Segunda columna
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f) // Ocupa el mismo espacio que la primera columna
                ) {
                    // Imagen para Crear Categoría
                    Image(
                        painter = painterResource(id = R.drawable.crear_categoria),
                        contentDescription = "Crear Categoría",
                        modifier = Modifier
                            .size(80.dp) // Tamaño de la imagen
                            .clickable {
                                val intent = Intent(context, AgregarCategoriaActivity::class.java)
                                context.startActivity(intent)
                            },
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    // Botón de Crear Categoría
                    Button(
                        onClick = {
                            val intent = Intent(context, AgregarCategoriaActivity::class.java)
                            context.startActivity(intent)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                        modifier = Modifier.fillMaxWidth(0.8f)
                    ) {
                        Text(text = "Crear Categoría", color = Color.White)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Imagen para Ver Categorías
                    Image(
                        painter = painterResource(id = R.drawable.ver_categoria),
                        contentDescription = "Ver Categorías",
                        modifier = Modifier
                            .size(80.dp) // Tamaño de la imagen
                            .clickable {
                                val intent = Intent(context, CategoriasActivity::class.java)
                                context.startActivity(intent)
                            },
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    // Botón de Ver Categorías
                    Button(
                        onClick = {
                            val intent = Intent(context, CategoriasActivity::class.java)
                            context.startActivity(intent)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                        modifier = Modifier.fillMaxWidth(0.8f)
                    ) {
                        Text(text = "Ver Categorías", color = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Fila para la opción de configuración y cerrar sesión
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly // Distribuir espacio uniformemente
            ) {
                // Imagen para Configuración
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.configuracion),
                        contentDescription = "Configuración",
                        modifier = Modifier
                            .size(80.dp) // Tamaño de la imagen
                            .clickable {
                                val intent = Intent(context, ConfiguracionActivity::class.java)
                                context.startActivity(intent)
                            },
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    // Botón de Configuración
                    Button(
                        onClick = {
                            val intent = Intent(context, ConfiguracionActivity::class.java)
                            context.startActivity(intent)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                        modifier = Modifier.fillMaxWidth() // Usar todo el ancho disponible
                    ) {
                        Text(
                            text = "Configuración",
                            color = Color.White,
                            modifier = Modifier.padding(vertical = 8.dp) // Ajustar el padding
                        )
                    }
                }

                // Imagen para Cerrar Sesión
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.cerrar_sesion),
                        contentDescription = "Cerrar Sesión",
                        modifier = Modifier
                            .size(80.dp) // Tamaño de la imagen
                            .clickable { onLogout() }, // Asegúrate de invocar la función aquí
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    // Botón de Cerrar Sesión
                    Button(
                        onClick = onLogout,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                        modifier = Modifier.fillMaxWidth() // Usar todo el ancho disponible
                    ) {
                        Text(
                            text = "Cerrar Sesión",
                            color = Color.White,
                            modifier = Modifier.padding(vertical = 8.dp) // Ajustar el padding
                        )
                    }
                }
            }
        }
    }
}
