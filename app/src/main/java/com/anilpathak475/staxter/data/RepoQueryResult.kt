package com.anilpathak475.staxter.data

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import com.anilpathak475.staxter.store.db.RepoEntity

data class RepoQueryResult(
    val data: LiveData<PagedList<RepoEntity>>,
    val isRequestInProgressLiveData: LiveData<Boolean>,
    val githubErrors: LiveData<Boolean>,
    val networkErrors: LiveData<Boolean>
)
