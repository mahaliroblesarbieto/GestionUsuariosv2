package com.example.gestionusuariosv2.locales.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.gestionusuariosv2.locales.storage.LocalUser

/**
 * Pantalla "Usuarios locales" (CRUD sobre Room). Portada del modulo de Mery
 * (puntos 8 Navegacion y 9 Arquitectura MVVM + Repository); solo se
 * armonizaron los colores con el tema indigo compartido de la app, la logica
 * y cada funcionalidad (buscar, alta, edicion, borrado con confirmacion)
 * quedan intactas.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocalUsersScreen(
    viewModel: LocalUsersViewModel,
    onHome: () -> Unit,
    onAdd: (() -> Unit)? = null,
    onEdit: ((LocalUser) -> Unit)? = null
) {
    val users by viewModel.users.collectAsStateWithLifecycle()
    var searchOpen by remember { mutableStateOf(false) }
    var query by remember { mutableStateOf("") }
    var drawer by remember { mutableStateOf(false) }
    var addSheet by remember { mutableStateOf(false) }
    var editSheet by remember { mutableStateOf<LocalUser?>(null) }
    var deleteTarget by remember { mutableStateOf<LocalUser?>(null) }
    val filtered = users.filter { it.fullName.contains(query, true) || it.username.contains(query, true) || it.email.contains(query, true) }

    val primary = MaterialTheme.colorScheme.primary
    val primaryContainer = MaterialTheme.colorScheme.primaryContainer
    val onPrimaryContainer = MaterialTheme.colorScheme.onPrimaryContainer

    Scaffold(containerColor = MaterialTheme.colorScheme.background, topBar = {
        TopAppBar(title = { Text("Usuarios locales", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold) },
            navigationIcon = { Box {
                IconButton(onClick = { drawer = true }) { Icon(Icons.Default.Menu, "Menú", tint = MaterialTheme.colorScheme.onPrimary) }
                DropdownMenu(expanded = drawer, onDismissRequest = { drawer = false }) {
                    DropdownMenuItem(text = { Text("Home") }, leadingIcon = { Icon(Icons.Default.Home, null) },
                        onClick = { drawer = false; onHome() })
                    DropdownMenuItem(text = { Text("Locales") }, leadingIcon = { Icon(Icons.Default.Storage, null) },
                        onClick = { drawer = false })
                }
            } },
            actions = { IconButton(onClick = { searchOpen = !searchOpen; if (!searchOpen) query = "" }) {
                Icon(Icons.Default.Search, "Buscar usuarios", tint = MaterialTheme.colorScheme.onPrimary)
            } },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = primary))
    }, bottomBar = {
        NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
            NavigationBarItem(false, onClick = onHome, icon = { Icon(Icons.Default.Home, null) }, label = { Text("Home") },
                colors = NavigationBarItemDefaults.colors(selectedIconColor = primary, selectedTextColor = primary, indicatorColor = primaryContainer))
            NavigationBarItem(true, onClick = {}, icon = { Icon(Icons.Default.Storage, null) }, label = { Text("Locales") },
                colors = NavigationBarItemDefaults.colors(selectedIconColor = primary, selectedTextColor = primary, indicatorColor = primaryContainer))
        }
    }, floatingActionButton = {
        FloatingActionButton(onClick = { if (onAdd == null) addSheet = true else onAdd() },
            containerColor = primary, contentColor = MaterialTheme.colorScheme.onPrimary, shape = CircleShape) {
            Icon(Icons.Default.Add, "Registrar usuario")
        }
    }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            if (searchOpen) OutlinedTextField(query, { query = it }, Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Nombre, usuario o correo") }, singleLine = true,
                trailingIcon = { IconButton(onClick = { query = "" }) { Icon(Icons.Default.Close, "Limpiar búsqueda") } })
            Row(Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("Directorio del dispositivo", color = primary, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    Text("Usuarios registrados en Room", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
                }
                Surface(color = primaryContainer, shape = RoundedCornerShape(16.dp)) {
                    Text("${users.size}", Modifier.padding(horizontal = 12.dp, vertical = 6.dp), color = onPrimaryContainer, fontWeight = FontWeight.Bold)
                }
            }
            if (filtered.isEmpty()) Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.People, null, tint = primary, modifier = Modifier.size(44.dp))
                    Spacer(Modifier.height(8.dp))
                    Text(if (query.isBlank()) "Aún no hay usuarios. Pulsa + para crear uno." else "No hay resultados.")
                }
            } else LazyColumn(contentPadding = PaddingValues(start = 14.dp, end = 14.dp, bottom = 90.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(filtered, key = { it.id }) { user ->
                    UserTile(user, onEdit = { if (onEdit == null) editSheet = user else onEdit(user) }, onDelete = { deleteTarget = user })
                }
            }
        }
    }

    deleteTarget?.let { user ->
        AlertDialog(onDismissRequest = { deleteTarget = null },
            icon = { Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error) },
            title = { Text("Eliminar usuario") },
            text = { Text("¿Estás seguro de que deseas eliminar este usuario? Esta acción no se puede deshacer.") },
            confirmButton = { Button(onClick = { viewModel.delete(user.id) { deleteTarget = null } },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) { Text("Eliminar") } },
            dismissButton = { OutlinedButton(onClick = { deleteTarget = null }) { Text("Cancelar") } })
    }
    if (addSheet || editSheet != null) {
        val current = editSheet
        ModalBottomSheet(onDismissRequest = { addSheet = false; editSheet = null; viewModel.clearError() }) {
            UserSheet(current, viewModel, onDone = { addSheet = false; editSheet = null })
        }
    }
}

@Composable
private fun UserTile(user: LocalUser, onEdit: () -> Unit, onDelete: () -> Unit) {
    var menu by remember { mutableStateOf(false) }
    val primary = MaterialTheme.colorScheme.primary
    val primaryContainer = MaterialTheme.colorScheme.primaryContainer
    Surface(Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(14.dp), shadowElevation = 1.dp) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.width(5.dp).height(86.dp).background(primaryContainer))
            Box(Modifier.padding(start = 12.dp).size(40.dp).background(primaryContainer, CircleShape), contentAlignment = Alignment.Center) {
                Text(user.fullName.firstOrNull()?.uppercase() ?: "?", color = primary, fontWeight = FontWeight.Bold)
            }
            Column(Modifier.weight(1f).padding(start = 12.dp, top = 10.dp, bottom = 10.dp)) {
                Text(user.fullName, color = primary, fontWeight = FontWeight.Bold)
                Text(user.username, style = MaterialTheme.typography.bodySmall)
                Text(user.email, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Box(Modifier.padding(end = 4.dp)) {
                IconButton(onClick = { menu = true }) { Icon(Icons.Default.MoreVert, "Opciones de ${user.username}") }
                DropdownMenu(expanded = menu, onDismissRequest = { menu = false }) {
                    DropdownMenuItem(text = { Text("Editar") }, leadingIcon = { Icon(Icons.Default.Edit, null) },
                        onClick = { menu = false; onEdit() })
                    DropdownMenuItem(text = { Text("Eliminar") }, leadingIcon = { Icon(Icons.Default.Delete, null) },
                        onClick = { menu = false; onDelete() })
                }
            }
        }
    }
}

@Composable
private fun UserSheet(existing: LocalUser?, vm: LocalUsersViewModel, onDone: () -> Unit) {
    var fullName by remember(existing) { mutableStateOf(existing?.fullName.orEmpty()) }
    var username by remember(existing) { mutableStateOf(existing?.username.orEmpty()) }
    var email by remember(existing) { mutableStateOf(existing?.email.orEmpty()) }
    var password by remember(existing) { mutableStateOf(existing?.password.orEmpty()) }
    val error by vm.error.collectAsStateWithLifecycle()
    val busy by vm.busy.collectAsStateWithLifecycle()
    Column(Modifier.fillMaxWidth().padding(20.dp)) {
        Text(if (existing == null) "Nuevo usuario" else "Editar usuario", color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(fullName, { fullName = it }, Modifier.fillMaxWidth(), label = { Text("Nombre completo") }, singleLine = true)
        OutlinedTextField(username, { username = it }, Modifier.fillMaxWidth(), label = { Text("Usuario") }, singleLine = true)
        OutlinedTextField(email, { email = it }, Modifier.fillMaxWidth(), label = { Text("Correo electrónico") }, singleLine = true)
        OutlinedTextField(password, { password = it }, Modifier.fillMaxWidth(), label = { Text("Contraseña") }, singleLine = true,
            visualTransformation = PasswordVisualTransformation())
        if (error != null) Text(error.orEmpty(), color = MaterialTheme.colorScheme.error)
        Spacer(Modifier.height(14.dp))
        Button(onClick = { vm.save(LocalUser(existing?.id ?: 0, fullName, username, email, password), onDone) },
            enabled = !busy, modifier = Modifier.fillMaxWidth()) { Text(if (existing == null) "GUARDAR" else "ACTUALIZAR") }
        Spacer(Modifier.height(22.dp))
    }
}
