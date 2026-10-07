package com.example.gestionusuariosv2.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.gestionusuariosv2.data.repository.ApiUserRepository

class ApiUserViewModelFactory(
    private val repository: ApiUserRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (
            modelClass.isAssignableFrom(
                ApiUserViewModel::class.java
            )
        ) {

            return ApiUserViewModel(
                repository
            ) as T

        }

        throw IllegalArgumentException(
            "ViewModel desconocido"
        )

    }

}

