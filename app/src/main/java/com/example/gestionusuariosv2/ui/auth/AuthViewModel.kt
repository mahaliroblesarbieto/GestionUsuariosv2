package com.example.gestionusuariosv2.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.gestionusuariosv2.data.local.UserEntity
import com.example.gestionusuariosv2.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel de la capa de autenticacion (Registro + Login).
 * Expone un StateFlow independiente por formulario para que cada pantalla
 * reaccione solo a lo que le corresponde.
 */
class AuthViewModel(private val repository: UserRepository) : ViewModel() {

    private val _registerState = MutableStateFlow(AuthUiState())
    val registerState: StateFlow<AuthUiState> = _registerState.asStateFlow()

    private val _loginState = MutableStateFlow(AuthUiState())
    val loginState: StateFlow<AuthUiState> = _loginState.asStateFlow()

    private val _loggedInUsername = MutableStateFlow<String?>(null)
    val loggedInUsername: StateFlow<String?> = _loggedInUsername.asStateFlow()

    fun register(
        username: String,
        email: String,
        password: String,
        confirmPassword: String
    ) {
        val validationError = validateRegister(username, email, password, confirmPassword)
        if (validationError != null) {
            _registerState.value = AuthUiState(errorMessage = validationError)
            return
        }

        viewModelScope.launch {
            _registerState.value = AuthUiState(isLoading = true)

            val existingUser = repository.getUserByUsername(username.trim())
            if (existingUser != null) {
                _registerState.value = AuthUiState(errorMessage = "Ese nombre de usuario ya esta registrado")
                return@launch
            }

            val newUser = UserEntity(
                username = username.trim(),
                email = email.trim(),
                password = password
            )

            _registerState.value = repository.register(newUser).fold(
                onSuccess = { AuthUiState(isSuccess = true) },
                onFailure = { AuthUiState(errorMessage = "No se pudo registrar el usuario, intenta nuevamente") }
            )
        }
    }

    fun registerNewUser(
        fullName: String,
        username: String,
        email: String,
        password: String,
        onResult: (success: Boolean, error: String?) -> Unit
    ) {
        if (fullName.isBlank() || username.isBlank() || email.isBlank() || password.isBlank()) {
            onResult(false, "Completa todos los campos")
            return
        }

        viewModelScope.launch {
            val existingUser = repository.getUserByUsername(username.trim())
            if (existingUser != null) {
                onResult(false, "Ese nombre de usuario ya esta registrado")
                return@launch
            }

            val newUser = UserEntity(
                fullName = fullName.trim(),
                username = username.trim(),
                email = email.trim(),
                password = password
            )

            repository.register(newUser).fold(
                onSuccess = { onResult(true, null) },
                onFailure = { onResult(false, "No se pudo registrar el usuario, intenta nuevamente") }
            )
        }
    }

    fun login(username: String, password: String) {
        if (username.isBlank() || password.isBlank()) {
            _loginState.value = AuthUiState(errorMessage = "Ingresa tu usuario y contraseña")
            return
        }

        viewModelScope.launch {
            _loginState.value = AuthUiState(isLoading = true)

            val user = repository.login(username.trim(), password)
            if (user != null) {
                _loggedInUsername.value = user.username
                _loginState.value = AuthUiState(isSuccess = true)
            } else {
                _loginState.value = AuthUiState(errorMessage = "Usuario o contraseña incorrectos")
            }
        }
    }

    fun resetRegisterState() {
        _registerState.value = AuthUiState()
    }

    fun resetLoginState() {
        _loginState.value = AuthUiState()
    }

    fun logout() {
        _loggedInUsername.value = null
    }

    private fun validateRegister(
        username: String,
        email: String,
        password: String,
        confirmPassword: String
    ): String? = when {
        username.isBlank() || email.isBlank() || password.isBlank() || confirmPassword.isBlank() ->
            "Completa todos los campos"
        !email.contains("@") || !email.contains(".") ->
            "Ingresa un correo electronico valido"
        password.length < 6 ->
            "La contraseña debe tener al menos 6 caracteres"
        password != confirmPassword ->
            "Las contraseñas no coinciden"
        else -> null
    }
}

class AuthViewModelFactory(private val repository: UserRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
