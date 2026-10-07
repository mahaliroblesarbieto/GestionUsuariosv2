package com.example.gestionusuariosv2.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.gestionusuariosv2.data.local.AppDatabase
import com.example.gestionusuariosv2.data.repository.UserRepository
import com.example.gestionusuariosv2.locales.domain.RoomLocalUsersRepository
import com.example.gestionusuariosv2.locales.presentation.LocalUsersFactory
import com.example.gestionusuariosv2.locales.presentation.LocalUsersScreen
import com.example.gestionusuariosv2.locales.presentation.LocalUsersViewModel
import com.example.gestionusuariosv2.locales.storage.LocalUsersDatabase
import com.example.gestionusuariosv2.ui.auth.AuthViewModel
import com.example.gestionusuariosv2.ui.auth.AuthViewModelFactory
import com.example.gestionusuariosv2.ui.auth.LoginScreen
import com.example.gestionusuariosv2.ui.auth.RegisterScreen
import com.example.gestionusuariosv2.ui.home.HomeTabScreen
import com.example.gestionusuariosv2.ui.screens.NuevoUsuarioScreen

/**
 * Grafo de navegacion de la app: Registro -> Login -> (Home | Locales).
 * Home y Locales comparten el mismo AuthViewModel para conocer la sesion
 * activa (loggedInUsername) sin tener que pasarla por argumentos de ruta.
 */
@Composable
fun AppNavGraph(navController: NavHostController = rememberNavController()) {
    val context = LocalContext.current

    val userRepository = remember { UserRepository(AppDatabase.getInstance(context).userDao()) }
    val authViewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory(userRepository))

    val localUsersRepository = remember { RoomLocalUsersRepository(LocalUsersDatabase.get(context).users()) }
    val localUsersViewModel: LocalUsersViewModel = viewModel(factory = LocalUsersFactory(localUsersRepository))

    NavHost(navController = navController, startDestination = Routes.LOGIN) {

        composable(Routes.REGISTER) {
            RegisterScreen(
                viewModel = authViewModel,
                onNavigateToLogin = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.REGISTER) { inclusive = true }
                    }
                },
                onRegisterSuccess = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.REGISTER) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.LOGIN) {
            LoginScreen(
                viewModel = authViewModel,
                onNavigateToRegister = { navController.navigate(Routes.REGISTER) },
                onLoginSuccess = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.HOME) {
            val username by authViewModel.loggedInUsername.collectAsStateWithLifecycle()
            HomeTabScreen(
                username = username.orEmpty(),
                onLocales = {
                    navController.navigate(Routes.LOCALES) { launchSingleTop = true }
                },
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(Routes.LOGIN) { popUpTo(0) }
                },
                onNuevoUsuario = {
                    navController.navigate(Routes.NUEVO_USUARIO)
                }
            )
        }

        composable(Routes.NUEVO_USUARIO) {
            var errorMessage by remember { mutableStateOf<String?>(null) }
            NuevoUsuarioScreen(
                onVolver = { navController.popBackStack() },
                onGuardar = { nombreCompleto, usuario, correo, contrasena ->
                    authViewModel.registerNewUser(nombreCompleto, usuario, correo, contrasena) { success, error ->
                        if (success) {
                            navController.popBackStack()
                        } else {
                            errorMessage = error
                        }
                    }
                },
                errorMessage = errorMessage
            )
        }

        composable(Routes.LOCALES) {
            LocalUsersScreen(
                viewModel = localUsersViewModel,
                onHome = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.HOME) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}
