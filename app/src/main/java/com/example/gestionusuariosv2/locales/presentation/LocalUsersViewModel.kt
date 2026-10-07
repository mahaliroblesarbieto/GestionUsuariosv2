package com.example.gestionusuariosv2.locales.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.gestionusuariosv2.locales.domain.LocalUsersRepository
import com.example.gestionusuariosv2.locales.storage.LocalUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LocalUsersViewModel(private val repository: LocalUsersRepository) : ViewModel() {
    val users = repository.users.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()
    private val _busy = MutableStateFlow(false)
    val busy = _busy.asStateFlow()

    fun clearError() { _error.value = null }
    fun save(user: LocalUser, onSaved: () -> Unit) {
        if (_busy.value) return
        viewModelScope.launch {
            _busy.value = true
            try {
                _error.value = repository.save(user)
                if (_error.value == null) onSaved()
            } finally { _busy.value = false }
        }
    }
    fun delete(id: Long, onDeleted: () -> Unit) = viewModelScope.launch {
        repository.delete(id)
        onDeleted()
    }
}

class LocalUsersFactory(private val repository: LocalUsersRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = LocalUsersViewModel(repository) as T
}
