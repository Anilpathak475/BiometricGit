package com.anilpathak475.staxter.store.db

import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.paging.DataSource
import io.reactivex.Single
import javax.inject.Inject

class LocalDataSource @Inject constructor(
    private val usersDao: UsersDao,
    private val sharedPreferences: SharedPreferences,
    private val reposDao: ReposDao
) {
    fun deleteAllUsers() {
        usersDao.deleteAll()
    }

    fun getRegisteredUser(): Single<UserEntity> {
        return usersDao.getRegisteredUser()
    }

    fun getUserByEmail(email: String): Single<List<UserEntity>> {
        return usersDao.getUserByEmail(email)
    }

    fun insertUser(userEntity: UserEntity) {
        usersDao.insert(userEntity)
    }

    fun loginUserState(logIn: Boolean) {
        sharedPreferences.edit {
            putBoolean(USER_LOG_IN, logIn)
        }
    }

    fun isUserLogIn(): Boolean {
        return sharedPreferences.getBoolean(USER_LOG_IN, false)
    }

    fun getAllRepositoriesByStars(): Single<List<RepoEntity>> {
        return reposDao.getAllRepositoriesByStars()
    }

    fun getAllRepositoriesByStarsAsDataSourceOffline(): DataSource.Factory<Int, RepoEntity> {
        return reposDao.getAllRepositoriesByStarsAsDataSourceOffline()
    }

    fun getAllRepositoriesById(githubRepoId: String): Single<RepoEntity> {
        return reposDao.getAllRepositoriesById(githubRepoId)
    }

    fun deleteAllReposData() {
        reposDao.deleteAllReposData()
    }

    fun insertRepos(onlineRepos: List<RepoEntity>) {
        return reposDao.insert(onlineRepos)
    }

    companion object {
        private const val USER_LOG_IN = "USER_LOG_IN"
    }
}
