package com.example.taskiapp

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.ui.platform.LocalContext
import java.util.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon

class NotaActivity : ComponentActivity() {
    private lateinit var categoriaId: String // Declara categoriaId como lateinit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        categoriaId = intent.getStringExtra("categoriaId") ?: "" // Obtiene categoriaId del intent
        setContent {
            NotaScreen(onBackClick = { finish() }, categoriaId = categoriaId) // Pasa categoriaId aquí
        }
    }
}

@Composable
fun NotaScreen(onBackClick: () -> Unit, categoriaId: String) {
    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var fecha by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Obteniendo Firestore y el usuario actual
    val firestore = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val userId = auth.currentUser?.uid // Obtener el ID del usuario actual

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // Encabezado
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
                // Botón de regresar
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Regresar",
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Texto de "Agregar tu nota"
                Text(
                    text = "Agregar tu nota",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineSmall
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Campos de entrada para la nota
        OutlinedTextField(
            value = titulo,
            onValueChange = { titulo = it },
            label = { Text("Título") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = descripcion,
            onValueChange = { descripcion = it },
            label = { Text("Descripción") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Botón para seleccionar la fecha
        OutlinedButton(onClick = {
            val calendar = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog(
                context,
                { _, year, month, dayOfMonth ->
                    fecha = "$dayOfMonth/${month + 1}/$year" // Formato de fecha
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.show()
        }) {
            Text(text = if (fecha.isEmpty()) "Selecciona una fecha" else "Fecha: $fecha")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón para guardar la nota
        Button(
            onClick = {
                if (titulo.isNotBlank() && descripcion.isNotBlank() && fecha.isNotBlank() && userId != null) {
                    loading = true
                    val nuevaNota = mapOf(
                        "titulo" to titulo,
                        "descripcion" to descripcion,
                        "fecha" to fecha,
                        "userId" to userId,
                        "categoriaId" to categoriaId // Guardar el ID de la categoría
                    )
                    firestore.collection("notas")
                        .add(nuevaNota)
                        .addOnSuccessListener {
                            loading = false
                            Toast.makeText(context, "Nota guardada", Toast.LENGTH_SHORT).show()
                            onBackClick() // Volver atrás después de guardar
                        }
                        .addOnFailureListener { e ->
                            loading = false
                            Toast.makeText(context, "Error al guardar la nota: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(context, "Por favor completa todos los campos.", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !loading // Deshabilitar el botón si está cargando
        ) {
            Text(text = if (loading) "Guardando..." else "Guardar Nota")
        }
    }
}
