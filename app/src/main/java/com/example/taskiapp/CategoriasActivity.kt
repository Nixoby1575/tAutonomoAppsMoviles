package com.example.taskiapp
import com.example.taskiapp.data.Categoria
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.Modifier

fun intToColor(colorInt: Int): Color {
    return Color(colorInt)
}

class CategoriasActivity : ComponentActivity() {
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CategoriasScreen(
                onAddCategoria = { abrirAgregarCategoria() },
                onEditCategoria = { categoria -> editarCategoria(categoria) },
                onDeleteCategoria = { categoria -> borrarCategoria(categoria) },
                onViewNotas = { categoria -> verNotas(categoria) }, // Nueva función para ver notas
                onBackClick = { finish() } // Pasar la función para volver atrás
            )
        }
    }

    private fun abrirAgregarCategoria() {
        // Abrir la actividad de agregar categoría
        val intent = Intent(this, AgregarCategoriaActivity::class.java)
        startActivity(intent)
    }

    private fun editarCategoria(categoria: Categoria) {
        // Iniciar NotaActivity y pasar el ID de la categoría
        val intent = Intent(this, NotaActivity::class.java)
        intent.putExtra("categoriaId", categoria.id) // Pasar el ID de la categoría
        startActivity(intent)
    }

    private fun borrarCategoria(categoria: Categoria) {
        firestore.collection("categorias").document(categoria.id)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Categoría ${categoria.nombre} borrada", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al borrar la categoría: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun verNotas(categoria: Categoria) {
        // Iniciar la actividad que muestra las notas de la categoría
        val intent = Intent(this, NotasPorCategoriaActivity::class.java)
        intent.putExtra("categoriaId", categoria.id) // Pasar el ID de la categoría
        startActivity(intent)
    }
}

@Composable
fun CategoriasScreen(
    onAddCategoria: () -> Unit,
    onEditCategoria: (Categoria) -> Unit,
    onDeleteCategoria: (Categoria) -> Unit,
    onViewNotas: (Categoria) -> Unit,
    onBackClick: () -> Unit // Recibir la función para volver atrás
) {
    val categorias = remember { mutableStateListOf<Categoria>() }
    val firestore = FirebaseFirestore.getInstance()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        firestore.collection("categorias").addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.e("CategoriasActivity", "Error al cargar categorías: ${e.message}")
                Toast.makeText(context, "Error al cargar categorías: ${e.message}", Toast.LENGTH_SHORT).show()
                return@addSnapshotListener
            }

            snapshot?.let { docs ->
                categorias.clear()
                for (doc in docs) {
                    try {
                        // Crea un objeto Categoria a partir del documento
                        val categoria = doc.toObject(Categoria::class.java)

                        // Asigna el color usando el número directamente
                        categoria.color = doc.getLong("color")?.toInt() ?: 0 // Guarda como Int

                        categorias.add(categoria.copy(id = doc.id)) // Agrega la categoría a la lista
                    } catch (e: Exception) {
                        Log.e("CategoriasActivity", "Error al procesar documento ${doc.id}: ${e.message}")
                    }
                }
            } ?: run {
                Toast.makeText(context, "No se encontraron categorías", Toast.LENGTH_SHORT).show()
            }
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

                // Texto de "Categorías"
                Text(
                    text = "Categorías",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineSmall
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Lista de categorías
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            for (categoria in categorias) {
                CategoriaItem(
                    categoria = categoria,
                    onEdit = onEditCategoria,
                    onDelete = onDeleteCategoria,
                    onViewNotas = onViewNotas
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = onAddCategoria) {
                Text("Agregar Categoría")
            }
        }
    }
}

@Composable
fun CategoriaItem(
    categoria: Categoria,
    onEdit: (Categoria) -> Unit,
    onDelete: (Categoria) -> Unit,
    onViewNotas: (Categoria) -> Unit
) {
    // Contenedor con un fondo del color asignado a la categoría y sombra
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .shadow(4.dp, RoundedCornerShape(8.dp)), // Añadir sombra
        shape = RoundedCornerShape(8.dp),
        color = intToColor(categoria.color) // Color de fondo asignado
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .clickable { onViewNotas(categoria) }, // Navegar a la pantalla de notas al hacer clic
            horizontalAlignment = Alignment.Start
        ) {
            Text(text = "Nombre: ${categoria.nombre.ifEmpty { "Sin Nombre" }}", style = MaterialTheme.typography.titleMedium)


            Spacer(modifier = Modifier.height(8.dp))

            // Botones de Editar y Eliminar
            Row {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Editar",
                    modifier = Modifier
                        .clickable { onEdit(categoria) }
                        .padding(8.dp)
                )
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar",
                    modifier = Modifier
                        .clickable { onDelete(categoria) }
                        .padding(8.dp)
                )
            }
        }
    }
}

