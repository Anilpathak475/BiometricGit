package com.anilpathak475.staxter.store.db

import androidx.paging.DataSource
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Query
import io.reactivex.Single

@Entity(tableName = "repos")
data class RepoEntity(

    @PrimaryKey
    @ColumnInfo(name = "id")
    val repoId: Int,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "description")
    val description: String?,

    @ColumnInfo(name = "stars")
    val stars: Long
)

@Dao
interface ReposDao : BaseDao<RepoEntity> {

    @Query("SELECT * FROM repos ORDER BY stars DESC, name ASC")
    fun getAllRepositoriesByStarsAsDataSourceOffline(): DataSource.Factory<Int, RepoEntity>

    @Query("SELECT * FROM repos where id = :githubRepoId")
    fun getAllRepositoriesById(githubRepoId: String): Single<RepoEntity>

    @Query("DELETE FROM repos")
    fun deleteAllReposData()

    @Query("SELECT * FROM repos ORDER BY stars DESC, name ASC")
    fun getAllRepositoriesByStars(): Single<List<RepoEntity>>

}