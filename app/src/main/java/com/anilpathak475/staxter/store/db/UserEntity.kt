package com.anilpathak475.staxter.store.db

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Query
import io.reactivex.Single
import java.util.UUID

@Entity(tableName = "users")
data class UserEntity(

    @PrimaryKey
    @ColumnInfo(name = "id")
    var userId: UUID,

    @ColumnInfo(name = "email")
    var email: String,

    @ColumnInfo(name = "password")
    var password: String
)

@Dao
interface UsersDao : BaseDao<UserEntity> {
    @Query("SELECT * FROM users where email = :email")
    fun getUserByEmail(email: String): Single<List<UserEntity>>

    @Query("SELECT * FROM users")
    fun getRegisteredUser(): Single<UserEntity>

    @Query("DELETE FROM users")
    fun deleteAll()
}