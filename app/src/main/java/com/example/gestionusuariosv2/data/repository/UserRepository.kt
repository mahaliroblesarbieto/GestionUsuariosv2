package com.example.gestionusuariosv2.data.repository

import android.database.sqlite.SQLiteConstraintException
import com.example.gestionusuariosv2.data.local.UserDao
import com.example.gestionusuariosv2.data.local.UserEntity
import kotlinx.coroutines.flow.Flow

/**
 * Unica fuente de verdad sobre los usuarios de Room. Pensada para que el resto
 * del equipo (Home local, CRUD, edicion) reutilice los mismos metodos en vez
 * de volver a tocar el DAO directamente.
 */
class UserRepository(private val userDao: UserDao) {

    val allUsers: Flow<List<UserEntity>> = userDao.getAllUsers()

    suspend fun getUserByUsername(username: String): UserEntity? =
        userDao.getUserByUsername(username)

    suspend fun getUserById(id: Int): UserEntity? =
        userDao.getUserById(id)

    suspend fun register(user: UserEntity): Result<Unit> = try {
        userDao.insertUser(user)
        Result.success(Unit)
    } catch (e: SQLiteConstraintException) {
        Result.failure(e)
    }

    suspend fun login(username: String, password: String): UserEntity? =
        userDao.login(username, password)

    suspend fun updateUser(user: UserEntity) = userDao.updateUser(user)

    suspend fun deleteUser(user: UserEntity) = userDao.deleteUser(user)
}
