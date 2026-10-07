package com.example.gestionusuariosv2.locales.domain

import android.util.Patterns
import com.example.gestionusuariosv2.locales.storage.LocalUser
import com.example.gestionusuariosv2.locales.storage.LocalUserDao
import kotlinx.coroutines.flow.Flow

interface LocalUsersRepository {
    val users: Flow<List<LocalUser>>
    suspend fun save(user: LocalUser): String?
    suspend fun delete(id: Long)
}

class RoomLocalUsersRepository(private val dao: LocalUserDao) : LocalUsersRepository {
    override val users = dao.observeAll()

    override suspend fun save(user: LocalUser): String? {
        val cleaned = user.copy(fullName = user.fullName.trim(), username = user.username.trim().lowercase(), email = user.email.trim().lowercase())
        if (cleaned.fullName.isBlank() || cleaned.username.isBlank() || cleaned.email.isBlank() || cleaned.password.isBlank())
            return "Completa todos los campos."
        if (cleaned.username.any { it.isWhitespace() }) return "El usuario no puede contener espacios."
        if (!Patterns.EMAIL_ADDRESS.matcher(cleaned.email).matches()) return "El correo no es válido."
        if (cleaned.password.length < 6) return "La contraseña requiere al menos 6 caracteres."
        if (dao.duplicate(cleaned.username, cleaned.email, cleaned.id) != null) return "Usuario o correo ya registrado."
        return try {
            if (cleaned.id == 0L) dao.insert(cleaned) else dao.update(cleaned)
            null
        } catch (e: Exception) { "No se pudo guardar el usuario." }
    }

    override suspend fun delete(id: Long) = dao.delete(id)
}
