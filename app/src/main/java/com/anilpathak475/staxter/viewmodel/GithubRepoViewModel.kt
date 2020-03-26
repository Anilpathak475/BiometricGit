package com.anilpathak475.staxter.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.anilpathak475.staxter.data.GithubRepository
import com.anilpathak475.staxter.extension.isNotDisposed
import com.anilpathak475.staxter.store.db.RepoEntity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

class GithubRepoViewModel @Inject constructor(
    private val githubRepository: GithubRepository
) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    private var repoByIdDisposable: Disposable? = null

    private val _repo = MutableLiveData<RepoEntity>()
    val repo: LiveData<RepoEntity> = _repo

    fun getGithubRepositoryById(githubRepoId: String) {
        if (repoByIdDisposable.isNotDisposed()) {
            return
        }
        repoByIdDisposable =
            githubRepository.getGithubRepositoryByIdOffline(githubRepoId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { _repo.value = it },
                    { throwable -> Timber.d("Timer Error $throwable") }
                )
        repoByIdDisposable?.let { compositeDisposable.add(it) }

    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}
