package com.example.taskiapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.FirebaseFirestore

import androidx.compose.ui.Modifier

class AgregarCategoriaActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AgregarCategoriaScreen { nombre, color ->
                agregarCategoria(nombre, color)
            }
        }
    }

    private fun agregarCategoria(nombre: String, color: Color) {
        val firestore = FirebaseFirestore.getInstance()
        val context = applicationContext

        val categoriaData = hashMapOf(
            "nombre" to nombre,
            "color" to color.toArgb() // Convierte Color a int
        )

        firestore.collection("categorias")
            .add(categoriaData)
            .addOnSuccessListener {
                Toast.makeText(context, "Categoría creada", Toast.LENGTH_SHORT).show()
                finish() // Cierra AgregarCategoriaActivity
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error al crear categoría: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}

@Composable
fun AgregarCategoriaScreen(onAgregar: (String, Color) -> Unit) {
    var nombreCategoria by remember { mutableStateOf("") }
    var colorCategoria by remember { mutableStateOf(Color.Gray) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color.White),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = nombreCategoria,
            onValueChange = { nombreCategoria = it },
            label = { Text("Nombre de la categoría") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        ColorPicker { selectedColor ->
            colorCategoria = selectedColor
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            onAgregar(nombreCategoria, colorCategoria) // Llamar la función de agregar
        }) {
            Text(text = "Crear Categoría")
        }
    }
}

@Composable
fun ColorPicker(onColorSelected: (Color) -> Unit) {
    // Ejemplo simple de un selector de color
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
