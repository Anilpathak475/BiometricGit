package com.anilpathak475.staxter.data.api

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface GithubApi {
    @GET("search/repositories?q=stars:>1&s=stars&type=Repositories")
    fun loadGitHubRepositoriesByStart(
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): Single<ReposListResponse>
}