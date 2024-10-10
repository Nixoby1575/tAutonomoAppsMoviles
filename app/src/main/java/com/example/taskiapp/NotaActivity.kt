package com.example.taskiapp

import android.app.DatePickerDialog
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
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.ui.platform.LocalContext
import java.util.*

class NotaActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NotaScreen()
        }
    }
}

@Composable
fun NotaScreen() {
    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var fecha by remember { mutableStateOf("Selecciona una fecha") }
    val firestore = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = titulo,
            onValueChange = { titulo = it },
            label = { Text("Título") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = descripcion,
            onValueChange = { descripcion = it },
            label = { Text("Descripción") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            // Crear un DatePickerDialog
            val datePickerDialog = DatePickerDialog(
                context,
                { _, selectedYear, selectedMonth, selectedDay ->
                    // Actualizar la fecha seleccionada
                    fecha = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                },
                year,
                month,
                day
            )
            datePickerDialog.show()
        }) {
            Text(text = "Seleccionar Fecha")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Fecha seleccionada: $fecha")

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = {
            // Validar que el título y la descripción no estén vacíos
            if (titulo.isEmpty() || descripcion.isEmpty()) {
                Toast.makeText(context, "Por favor, completa todos los campos.", Toast.LENGTH_SHORT).show()
                return@Button
            }

            // Guardar nota en Firestore
            val notaData = hashMapOf(
                "titulo" to titulo,
                "descripcion" to descripcion,
                "fecha" to fecha,
                "userId" to auth.currentUser?.uid // Agregar el ID del usuario
            )

            firestore.collection("notas") // Nombre de la colección
                .add(notaData)
                .addOnSuccessListener { documentReference ->
                    Toast.makeText(context, "Nota guardada con ID: ${documentReference.id}", Toast.LENGTH_SHORT).show()
                    // Reiniciar los campos después de guardar
                    titulo = ""
                    descripcion = ""
                    fecha = "Selecciona una fecha"
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Error al guardar nota: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }) {
            Text(text = "Guardar Nota")
        }
    }
}

