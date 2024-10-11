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
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext

class MisNotasActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MisNotasScreen(onBackClick = { finish() }) // Pasar la función para volver atrás
        }
    }
}

@Composable
fun MisNotasScreen(onBackClick: () -> Unit) { // Recibir la función para volver atrás
    val firestore = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val userId = auth.currentUser?.uid // Obtener el ID del usuario actual
    var notas by remember { mutableStateOf(listOf<Nota>()) }
    var loading by remember { mutableStateOf(true) }
    var editandoNota by remember { mutableStateOf<Nota?>(null) } // Controlar si estamos editando una nota
    val context = LocalContext.current

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Encabezado con el color del encabezado de la pantalla de bienvenida
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

                // Texto de "Mis Notas"
                Text(
                    text = "Mis Notas",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineSmall
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (loading) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                CircularProgressIndicator() // Mostrar indicador de carga centrado
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                // Si no hay notas, mostrar un mensaje
                if (notas.isEmpty()) {
                    Text(text = "No tienes notas.", style = MaterialTheme.typography.bodyMedium)
                } else {
                    for (nota in notas) {
                        NotaItem(nota,
                            onEdit = { notaParaEditar ->
                                editandoNota = notaParaEditar // Iniciar el proceso de edición
                            },
                            onDelete = { notaParaEliminar ->
                                eliminarNota(notaParaEliminar, firestore, context)
                            })
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }

            // Si estamos editando una nota, mostramos el formulario
            editandoNota?.let { nota ->
                EditarNotaDialog(
                    nota = nota,
                    onSave = { tituloEditado, descripcionEditada ->
                        editarNota(nota.id, tituloEditado, descripcionEditada, firestore, context)
                        editandoNota = null // Salir del modo de edición
                    },
                    onCancel = {
                        editandoNota = null // Cancelar edición
                    }
                )
            }
        }
    }
}

// Función para eliminar una nota
fun eliminarNota(nota: Nota, firestore: FirebaseFirestore, context: android.content.Context) {
    firestore.collection("notas").document(nota.id)
        .delete()
        .addOnSuccessListener {
            Toast.makeText(context, "Nota eliminada", Toast.LENGTH_SHORT).show()
        }
        .addOnFailureListener { e ->
            Toast.makeText(context, "Error al eliminar nota: ${e.message}", Toast.LENGTH_SHORT).show()
        }
}

// Componente para mostrar cada nota
@Composable
fun NotaItem(nota: Nota, onEdit: (Nota) -> Unit, onDelete: (Nota) -> Unit) {
    // Contenedor con un fondo claro y sombra
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .shadow(4.dp, RoundedCornerShape(8.dp)), // Añadir sombra
        shape = RoundedCornerShape(8.dp),
        color = Color(0xFFE0E0E0) // Color de fondo claro
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(text = "Título: ${nota.titulo}", style = MaterialTheme.typography.titleMedium)
            Text(text = "Descripción: ${nota.descripcion}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Fecha: ${nota.fecha}", style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(8.dp))

            // Botones de Editar y Eliminar
            Row {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Editar",
                    modifier = Modifier
                        .clickable { onEdit(nota) }
                        .padding(8.dp)
                )
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar",
                    modifier = Modifier
                        .clickable { onDelete(nota) }
                        .padding(8.dp)
                )
            }
        }
    }
}


// Función para editar una nota
fun editarNota(notaId: String, nuevoTitulo: String, nuevaDescripcion: String, firestore: FirebaseFirestore, context: android.content.Context) {
    firestore.collection("notas").document(notaId)
        .update("titulo", nuevoTitulo, "descripcion", nuevaDescripcion)
        .addOnSuccessListener {
            Toast.makeText(context, "Nota actualizada", Toast.LENGTH_SHORT).show()
        }
        .addOnFailureListener { e ->
            Toast.makeText(context, "Error al actualizar nota: ${e.message}", Toast.LENGTH_SHORT).show()
        }
}

// Diálogo para editar una nota
@Composable
fun EditarNotaDialog(nota: Nota, onSave: (String, String) -> Unit, onCancel: () -> Unit) {
    var nuevoTitulo by remember { mutableStateOf(nota.titulo) }
    var nuevaDescripcion by remember { mutableStateOf(nota.descripcion) }

    AlertDialog(
        onDismissRequest = { onCancel() },
        title = { Text(text = "Editar Nota") },
        text = {
            Column {
                OutlinedTextField(
                    value = nuevoTitulo,
                    onValueChange = { nuevoTitulo = it },
                    label = { Text("Título") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = nuevaDescripcion,
                    onValueChange = { nuevaDescripcion = it },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = { onSave(nuevoTitulo, nuevaDescripcion) }) {
                Text("Guardar")
            }
        },
        dismissButton = {
            Button(onClick = onCancel) {
                Text("Cancelar")
            }
        }
    )
}

// Data class para representar las notas
data class Nota(
    val id: String,
    val titulo: String,
    val descripcion: String,
    val fecha: String
)
