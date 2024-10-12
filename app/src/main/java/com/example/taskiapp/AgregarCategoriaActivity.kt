package com.example.taskiapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.draw.shadow
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.ui.platform.LocalContext

class AgregarCategoriaActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AgregarCategoriaScreen(onBackClick = { finish() }) { nombre, color ->
                agregarCategoria(nombre, color)
            }
        }
    }

    private fun agregarCategoria(nombre: String, color: Color) {
        val firestore = FirebaseFirestore.getInstance()
        val context = applicationContext

        val categoriaData = hashMapOf(
            "nombre" to nombre,
            "color" to color.toArgb() // Convertir Color a int
        )

        firestore.collection("categorias")
            .add(categoriaData)
            .addOnSuccessListener {
                Toast.makeText(context, "Categoría creada", Toast.LENGTH_SHORT).show()
                finish() // Cierra la actividad
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error al crear categoría: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}

@Composable
fun AgregarCategoriaScreen(onBackClick: () -> Unit, onAgregar: (String, Color) -> Unit) {
    var nombreCategoria by remember { mutableStateOf("") }
    var colorCategoria by remember { mutableStateOf<Color?>(null) } // Cambiado a nullable para verificar si hay color seleccionado
    var isColorPickerExpanded by remember { mutableStateOf(false) } // Controla si el selector de color está desplegado o no
    val context = LocalContext.current

    // Verificar si ambos campos están completos para habilitar el botón
    val isFormValid = nombreCategoria.isNotBlank() && colorCategoria != null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color.White),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        // Encabezado con el color y botón de regresar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary) // Color del encabezado
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Regresar",
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = "Crear una categoría",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineSmall
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Campo de texto para el nombre de la categoría
        OutlinedTextField(
            value = nombreCategoria,
            onValueChange = { nombreCategoria = it },
            label = { Text("Nombre de la categoría") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Área de selección de color con texto y flecha
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(4.dp, RoundedCornerShape(8.dp)) // Sombra alrededor del contenedor
                .clickable { isColorPickerExpanded = !isColorPickerExpanded } // Al hacer clic, alterna el estado del selector
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Elige el color de tu categoría",
                    style = MaterialTheme.typography.bodyMedium
                )

                // Mostrar flecha según el estado del ColorPicker
                Icon(
                    imageVector = if (isColorPickerExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Mostrar el selector de colores si está expandido
        if (isColorPickerExpanded) {
            ColorPicker { selectedColor ->
                colorCategoria = selectedColor
                isColorPickerExpanded = false // Colapsa el selector de color al seleccionar uno
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón para crear la categoría
        Button(
            onClick = {
                onAgregar(nombreCategoria, colorCategoria ?: Color.Gray) // Llamar a la función de agregar
            },
            enabled = isFormValid, // Solo habilitar cuando el formulario sea válido
            colors = ButtonDefaults.buttonColors(
                disabledContainerColor = Color.Gray, // Color cuando está deshabilitado
                containerColor = MaterialTheme.colorScheme.primary // Color normal
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Crear Categoría")
        }
    }
}

@Composable
fun ColorPicker(onColorSelected: (Color) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
        ColorSwatch(Color.Red) { onColorSelected(it) }
        ColorSwatch(Color.Green) { onColorSelected(it) }
        ColorSwatch(Color.Blue) { onColorSelected(it) }
        ColorSwatch(Color.Yellow) { onColorSelected(it) }
    }
}

@Composable
fun ColorSwatch(color: Color, onClick: (Color) -> Unit) {
    Box(
        modifier = Modifier
            .size(50.dp)
            .background(color)
            .clickable { onClick(color) }
    )
}
