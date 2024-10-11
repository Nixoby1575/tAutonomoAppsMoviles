package com.example.taskiapp

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
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.toArgb

class NotasPorCategoriaActivity : ComponentActivity() {
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val categoriaId = intent.getStringExtra("categoriaId") ?: ""
        val categoriaColor = intent.getIntExtra("categoriaColor", Color.Gray.toArgb())

        setContent {
            MaterialTheme {
                NotasPorCategoriaScreen(categoriaId, onBackClick = { finish() })
            }
        }
    }
}

@Composable
fun NotasPorCategoriaScreen(categoriaId: String, onBackClick: () -> Unit) {
    val firestore = FirebaseFirestore.getInstance()
    var notas by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }

    val context = LocalContext.current

    // Consulta para obtener las notas filtradas por categoría
    LaunchedEffect(categoriaId) {
        firestore.collection("notas")
            .whereEqualTo("categoriaId", categoriaId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                notas = querySnapshot.documents.map { it.data ?: emptyMap() }
                loading = false
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error al cargar notas: ${e.message}", Toast.LENGTH_SHORT).show()
                loading = false
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Encabezado con la flecha de regreso y el color que coincide con NotasActivity
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary) // Usar el color de NotasActivity
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBackClick) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Regresar", tint = Color.White)
                }

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = "Notas de la categoría",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Contenido de las notas
        if (loading) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(modifier = Modifier.padding(16.dp)) {
                for (nota in notas) {
                    NotaItem(nota)
                }
            }
        }
    }
}

@Composable
fun NotaItem(nota: Map<String, Any>) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .shadow(4.dp, RoundedCornerShape(8.dp)), // Sombra alrededor de la nota
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            val titulo = nota["titulo"]?.toString() ?: "Sin título"
            val descripcion = nota["descripcion"]?.toString() ?: "Sin descripción"

            Text(text = "Título: $titulo", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Descripción: $descripcion", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
