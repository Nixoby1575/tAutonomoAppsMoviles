package com.example.taskiapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MisNotasActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MisNotasScreen()
        }
    }
}

@Composable
fun MisNotasScreen() {
    val firestore = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val userId = auth.currentUser?.uid // Obtener el ID del usuario actual
    var notas by remember { mutableStateOf(listOf<Nota>()) }
    var loading by remember { mutableStateOf(true) }

    // Recuperar notas del Firestore filtradas por userId
    LaunchedEffect(Unit) {
        if (userId != null) {
            firestore.collection("notas")
                .whereEqualTo("userId", userId) // Filtrar por userId
                .get()
                .addOnSuccessListener { querySnapshot ->
                    notas = querySnapshot.documents.map { doc ->
                        Nota(
                            id = doc.id,
                            titulo = doc.getString("titulo") ?: "",
                            descripcion = doc.getString("descripcion") ?: "",
                            fecha = doc.getString("fecha") ?: ""
                        )
                    }
                    loading = false
                }
                .addOnFailureListener {
                    loading = false
                }
        } else {
            loading = false // Si no hay usuario, no se carga nada
        }
    }

    if (loading) {
        CircularProgressIndicator() // Mostrar indicador de carga
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            Text(text = "Mis Notas", style = MaterialTheme.typography.headlineSmall)

            Spacer(modifier = Modifier.height(16.dp))

            // Si no hay notas, mostrar un mensaje
            if (notas.isEmpty()) {
                Text(text = "No tienes notas.", style = MaterialTheme.typography.bodyMedium)
            } else {
                for (nota in notas) {
                    NotaItem(nota)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun NotaItem(nota: Nota) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(text = "Título: ${nota.titulo}", style = MaterialTheme.typography.titleMedium)
        Text(text = "Descripción: ${nota.descripcion}", style = MaterialTheme.typography.bodyMedium)
        Text(text = "Fecha: ${nota.fecha}", style = MaterialTheme.typography.bodyMedium)
    }
}

data class Nota(
    val id: String,
    val titulo: String,
    val descripcion: String,
    val fecha: String
)
