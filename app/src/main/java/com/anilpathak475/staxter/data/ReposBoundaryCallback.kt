package com.anilpathak475.staxter.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import com.anilpathak475.staxter.data.api.ReposResponse
import com.anilpathak475.staxter.data.model.login.ErrorType
import com.anilpathak475.staxter.store.InternalErrorConverter
import com.anilpathak475.staxter.store.db.RepoEntity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

class ReposBoundaryCallback @Inject constructor(
    private val repository: GithubRepository,
    private val internalErrorConverter: InternalErrorConverter
) : PagedList.BoundaryCallback<RepoEntity>() {

    private val compositeDisposable = CompositeDisposable()

    companion object {
        private const val NETWORK_PAGE_SIZE = 50
    }

    private var lastRequestedPage = 1

    private val _githubError = MutableLiveData<Boolean>()
    val githubError: LiveData<Boolean> = _githubError

    private val _networkError = MutableLiveData<Boolean>()
    val networkError: LiveData<Boolean> = _networkError

    private var isRequestInProgress = false

    private val _isRequestInProgress = MutableLiveData<Boolean>()
    val isRequestInProgressLiveData: LiveData<Boolean> = _isRequestInProgress
    /**
     * Database returned 0 items. We should query the backend for more items.
     */
    override fun onZeroItemsLoaded() {
        Timber.d("RepoBoundaryCallback: onZeroItemsLoaded")
        requestAndSaveData()
    }

    /**s
     * When all items in the database were loaded, we need to query the backend for more items.
     */
    override fun onItemAtEndLoaded(itemAtEnd: RepoEntity) {
        Timber.d("RepoBoundaryCallback: onItemAtEndLoaded")
        requestAndSaveData()
    }

    private fun requestAndSaveData() {
        if (isRequestInProgress) return

        isRequestInProgress = true
        _isRequestInProgress.value = true

        val disposable = repository.getRepositoriesOnline(
            lastRequestedPage,
            NETWORK_PAGE_SIZE
        )
            .map { onlineRepos ->
                Timber.d("Repo Saver. Number of retrieved online Repos : ${onlineRepos.count()}")
                onlineRepos.mapToEntities()
            }
            .flatMapCompletable { reposEntities ->
                Timber.d("Repo Saver. Number of repos saved offline : ${reposEntities.count()}")
                repository.saveOffline(reposEntities)
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    lastRequestedPage++
                    isRequestInProgress = false
                    _isRequestInProgress.value = false
                },
                { throwable ->
                    when (internalErrorConverter.convertToGeneralErrorType(throwable)) {
                        ErrorType.SERVER_CONNECTION -> {
                            _networkError.value = true
                        }
                        else -> {
                            _githubError.value = true
                        }
                    }
                    isRequestInProgress = false
                    _isRequestInProgress.value = false
                    Timber.d("Repo Saver. Error : ${throwable.message}")
                }
            )

        compositeDisposable.add(disposable)
    }

    fun onCleared() {
        compositeDisposable.clear()
    }
}

private fun List<ReposResponse>.mapToEntities(): List<RepoEntity> {
    return map {
        it.mapToEntity()
    }
}

private fun ReposResponse.mapToEntity(): RepoEntity {
    return RepoEntity(
        repoId = id,
        name = name,
        description = description,
        stars = stars
    )
}
