package com.example.gestionusuariosv2.locales.storage

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Index
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Directorio local de usuarios para la pantalla "Locales" (independiente de las
 * cuentas de Registro/Login, que viven en data/local/UserEntity). Portado del
 * modulo de Mery (arquitectura MVVM + Repository + base de Room, punto 8 y 9
 * del reparto del equipo).
 */
@Entity(tableName = "local_users", indices = [Index(value = ["username"], unique = true), Index(value = ["email"], unique = true)])
data class LocalUser(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val fullName: String,
    val username: String,
    val email: String,
    val password: String
)

@Dao
interface LocalUserDao {
    @Query("SELECT * FROM local_users ORDER BY fullName COLLATE NOCASE")
    fun observeAll(): Flow<List<LocalUser>>

    @Query("SELECT * FROM local_users WHERE (username = :username COLLATE NOCASE OR email = :email COLLATE NOCASE) AND id != :exceptId LIMIT 1")
    suspend fun duplicate(username: String, email: String, exceptId: Long): LocalUser?

    @Insert suspend fun insert(user: LocalUser)
    @Update suspend fun update(user: LocalUser)
    @Query("DELETE FROM local_users WHERE id = :id") suspend fun delete(id: Long)
}

@Database(entities = [LocalUser::class], version = 1, exportSchema = false)
abstract class LocalUsersDatabase : RoomDatabase() {
    abstract fun users(): LocalUserDao

    companion object {
        @Volatile private var instance: LocalUsersDatabase? = null
        fun get(context: Context): LocalUsersDatabase = instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(context.applicationContext, LocalUsersDatabase::class.java, "locales_users.db")
                .build().also { instance = it }
        }
    }
}
