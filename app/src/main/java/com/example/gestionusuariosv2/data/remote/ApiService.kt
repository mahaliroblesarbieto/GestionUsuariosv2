package com.example.gestionusuariosv2.data.remote

import com.example.gestionusuariosv2.data.model.ApiUser
import retrofit2.http.GET

interface ApiService {

    @GET("users")
    suspend fun getUsers(): List<ApiUser>

}

