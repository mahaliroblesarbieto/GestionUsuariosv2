package com.example.gestionusuariosv2.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gestionusuariosv2.data.remote.RetrofitClient
import com.example.gestionusuariosv2.data.repository.ApiUserRepositoryImpl
import com.example.gestionusuariosv2.ui.components.ApiUserCard
import com.example.gestionusuariosv2.viewmodel.ApiUserViewModel
import com.example.gestionusuariosv2.viewmodel.ApiUserViewModelFactory

private val AvatarColors = listOf(
    Color(0xFF5626D9),
    Color(0xFFFF6D00),
    Color(0xFF087F4B),
    Color(0xFF0874C9),
    Color(0xFFE83B45)
)

/**
 * Pestaña "Home": lista de usuarios desde JSONPlaceholder (Retrofit) con un
 * acceso directo para registrar un nuevo usuario local (punto 5 del reparto).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTabScreen(
    username: String,
    onLocales: () -> Unit,
    onLogout: () -> Unit,
    onNuevoUsuario: () -> Unit = {}
) {
    val primary = MaterialTheme.colorScheme.primary
    val primaryContainer = MaterialTheme.colorScheme.primaryContainer

    val repository = remember { ApiUserRepositoryImpl(RetrofitClient.apiService) }
    val viewModel: ApiUserViewModel = viewModel(factory = ApiUserViewModelFactory(repository))

    val users by viewModel.users.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getUsers()
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Usuarios desde API", color = MaterialTheme.colorScheme.onPrimary) },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.AutoMirrored.Filled.Logout, "Cerrar sesión", tint = MaterialTheme.colorScheme.onPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = primary)
            )
        },
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                NavigationBarItem(
                    selected = true,
                    onClick = {},
                    icon = { Icon(Icons.Default.Home, null) },
                    label = { Text("Home") },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = primary, selectedTextColor = primary, indicatorColor = primaryContainer)
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onLocales,
                    icon = { Icon(Icons.Default.Storage, null) },
                    label = { Text("Locales") },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = primary, selectedTextColor = primary, indicatorColor = primaryContainer)
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNuevoUsuario,
                containerColor = primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, "Registrar nuevo usuario")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            Text(
                text = "Sesión iniciada como $username",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 16.dp, top = 12.dp, bottom = 6.dp)
            )

            when {
                isLoading -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = primary)
                }

                errorMessage != null -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = errorMessage.orEmpty(),
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                users.isEmpty() -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay usuarios para mostrar.")
                }

                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 12.dp, end = 12.dp, bottom = 90.dp),
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    itemsIndexed(users) { index, user ->
                        ApiUserCard(
                            user = user,
                            colorInicial = AvatarColors[index % AvatarColors.size]
                        )
                    }
                }
            }
        }
    }
}
