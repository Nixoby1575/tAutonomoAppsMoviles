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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.draw.shadow
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp

class AgregarCategoriaActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AgregarCategoriaScreen(
                onBackClick = { finish() },
                onAgregar = { nombre, color ->
                    agregarCategoria(nombre, color)
                }
            )
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
    var colorCategoria by remember { mutableStateOf<Color?>(null) }
    var isColorPickerExpanded by remember { mutableStateOf(false) }
    val isFormValid = nombreCategoria.isNotBlank() && colorCategoria != null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color.White),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
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

        OutlinedTextField(
            value = nombreCategoria,
            onValueChange = { nombreCategoria = it },
            label = { Text("Nombre de la categoría") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(4.dp, RoundedCornerShape(8.dp))
                .clickable { isColorPickerExpanded = !isColorPickerExpanded }
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
                Icon(
                    imageVector = if (isColorPickerExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (isColorPickerExpanded) {
            ColorPicker { selectedColor ->
                colorCategoria = selectedColor
                isColorPickerExpanded = false
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { onAgregar(nombreCategoria, colorCategoria ?: Color.Gray) },
            enabled = isFormValid,
            colors = ButtonDefaults.buttonColors(
                disabledContainerColor = Color.Gray,
                containerColor = MaterialTheme.colorScheme.primary
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
        ColorSwatch(Color.Red, onColorSelected)
        ColorSwatch(Color.Green, onColorSelected)
        ColorSwatch(Color.Blue, onColorSelected)
        ColorSwatch(Color.Yellow, onColorSelected)
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
