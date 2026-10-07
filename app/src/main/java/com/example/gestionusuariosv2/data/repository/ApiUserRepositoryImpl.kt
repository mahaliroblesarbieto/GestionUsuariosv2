package com.example.gestionusuariosv2.data.repository

import com.example.gestionusuariosv2.data.model.ApiUser
import com.example.gestionusuariosv2.data.remote.ApiService

class ApiUserRepositoryImpl(
    private val apiService: ApiService
) : ApiUserRepository {

    override suspend fun getUsers(): List<ApiUser> {
        return apiService.getUsers()

    }

}
