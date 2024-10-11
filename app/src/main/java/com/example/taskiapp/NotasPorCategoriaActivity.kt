package com.example.taskiapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.ui.platform.LocalContext

class NotasPorCategoriaActivity : ComponentActivity() {
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val categoriaId = intent.getStringExtra("categoriaId") ?: ""

        setContent {
            MaterialTheme { // Agregar el MaterialTheme
                NotasPorCategoriaScreen(categoriaId) // Asegúrate de pasar el ID correctamente
            }
        }
    }
}

@Composable
fun NotasPorCategoriaScreen(categoriaId: String) {
    val firestore = FirebaseFirestore.getInstance()
    var notas by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }

    // Obtener el contexto aquí
    val context = LocalContext.current

    LaunchedEffect(categoriaId) {
        firestore.collection("notas")
            .whereEqualTo("categoriaId", categoriaId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                notas = querySnapshot.documents.map { it.data ?: emptyMap() } // Guardar las notas en la lista
                loading = false
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error al cargar notas: ${e.message}", Toast.LENGTH_SHORT).show()
                loading = false
            }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        if (loading) {
            CircularProgressIndicator()
        } else {
            Text("Notas de la categoría", style = MaterialTheme.typography.headlineMedium)

            Spacer(modifier = Modifier.height(16.dp))

            // Lista de notas
            for (nota in notas) {
                NotaItem(nota)
            }
        }
    }
}

@Composable
fun NotaItem(nota: Map<String, Any>) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            val titulo = nota["titulo"]?.toString() ?: "Sin título"
            val descripcion = nota["descripcion"]?.toString() ?: "Sin descripción"

            Text(text = "Título: $titulo", style = MaterialTheme.typography.titleMedium)
            Text(text = "Descripción: $descripcion", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
