package com.anilpathak475.staxter.store.db

import com.anilpathak475.staxter.data.api.GithubApi
import com.anilpathak475.staxter.data.api.ReposListResponse
import io.reactivex.Single
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    private val githubApi: GithubApi
) {
    fun loadGitHubRepositoriesByStart(
        page: Int,
        requestedLoadSize: Int
    ): Single<ReposListResponse> {
        return githubApi.loadGitHubRepositoriesByStart(page, requestedLoadSize)
    }
}
