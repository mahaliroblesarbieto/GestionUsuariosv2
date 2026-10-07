package com.example.gestionusuariosv2.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Tabla unica de usuarios en Room. Se reutiliza tanto para las cuentas creadas
 * en el registro inicial (Mahali) como para los usuarios locales administrados
 * en el CRUD de "Locales" (Sheyla / Sandro), por eso [fullName] tiene valor por
 * defecto vacio: la pantalla de "Crear cuenta" no lo pide, pero "Nuevo usuario" si.
 */
@Entity(
    tableName = "users",
    indices = [Index(value = ["username"], unique = true)]
)
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val fullName: String = "",
    val username: String,
    val email: String,
    val password: String
)
