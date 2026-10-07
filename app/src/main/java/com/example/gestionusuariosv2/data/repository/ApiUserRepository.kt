package com.example.gestionusuariosv2.data.repository

import com.example.gestionusuariosv2.data.model.ApiUser

interface ApiUserRepository {

    suspend fun getUsers(): List<ApiUser>

}

