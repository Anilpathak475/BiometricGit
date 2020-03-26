package com.anilpathak475.staxter.data

import androidx.paging.DataSource
import com.anilpathak475.staxter.data.api.ReposResponse
import com.anilpathak475.staxter.store.db.RepoEntity
import io.reactivex.Completable
import io.reactivex.Single

interface GithubRepository {
    fun getRepositoriesOnline(page: Int, requestedLoadSize: Int): Single<List<ReposResponse>>
    fun getAllRepositoriesByStarsAsDataSourceOffline(): DataSource.Factory<Int, RepoEntity>
    fun getAllRepositoriesByStarsOffline(): Single<List<RepoEntity>>
    fun saveOffline(onlineRepos: List<RepoEntity>): Completable
    fun getGithubRepositoryByIdOffline(githubRepoId: String): Single<RepoEntity>
    fun clearAllData()
}