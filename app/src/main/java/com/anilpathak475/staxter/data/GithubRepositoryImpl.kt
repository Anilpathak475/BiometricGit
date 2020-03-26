package com.anilpathak475.staxter.data

import androidx.paging.DataSource
import com.anilpathak475.staxter.data.api.ReposResponse
import com.anilpathak475.staxter.store.db.LocalDataSource
import com.anilpathak475.staxter.store.db.RemoteDataSource
import com.anilpathak475.staxter.store.db.RepoEntity
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

class GithubRepositoryImpl @Inject constructor(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource
) : GithubRepository {

    override fun getRepositoriesOnline(
        page: Int,
        requestedLoadSize: Int
    ): Single<List<ReposResponse>> {
        return remoteDataSource.loadGitHubRepositoriesByStart(page, requestedLoadSize)
            .map { it.items }
    }

    override fun getAllRepositoriesByStarsAsDataSourceOffline(): DataSource.Factory<Int, RepoEntity> {
        return localDataSource.getAllRepositoriesByStarsAsDataSourceOffline()
    }

    override fun getAllRepositoriesByStarsOffline(): Single<List<RepoEntity>> {
        return localDataSource.getAllRepositoriesByStars()
    }

    override fun saveOffline(onlineRepos: List<RepoEntity>): Completable {
        return Completable.fromCallable { localDataSource.insertRepos(onlineRepos) }
    }

    override fun getGithubRepositoryByIdOffline(githubRepoId: String): Single<RepoEntity> {
        return localDataSource.getAllRepositoriesById(githubRepoId)
    }

    override fun clearAllData() {
        localDataSource.deleteAllReposData()
    }

}