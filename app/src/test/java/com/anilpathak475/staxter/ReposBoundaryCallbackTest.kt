package com.anilpathak475.staxter

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.anilpathak475.staxter.data.GithubRepositoryImpl
import com.anilpathak475.staxter.data.ReposBoundaryCallback
import com.anilpathak475.staxter.data.api.GithubApi
import com.anilpathak475.staxter.data.api.ReposResponse
import com.anilpathak475.staxter.store.InternalErrorConverter
import com.anilpathak475.staxter.store.db.AppDatabase
import com.anilpathak475.staxter.store.db.LocalDataSource
import com.anilpathak475.staxter.store.db.RemoteDataSource
import com.anilpathak475.staxter.store.db.RepoEntity
import com.anilpathak475.staxter.store.db.githubrepo.ReposDao
import com.anilpathak475.staxter.store.db.user.UsersDao
import com.google.common.truth.Truth
import io.mockk.every
import io.mockk.mockk
import io.reactivex.Single
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ReposBoundaryCallbackTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var usersDao: UsersDao
    private lateinit var reposDao: ReposDao
    private lateinit var db: AppDatabase

    private lateinit var githubRepository: GithubRepositoryImpl
    private lateinit var localDataSource: LocalDataSource
    private lateinit var remoteDataSource: RemoteDataSource
    private lateinit var githubApi: GithubApi
    private lateinit var internalErrorConverter: InternalErrorConverter

    private lateinit var reposBoundaryCallback: ReposBoundaryCallback

    @Before
    fun setUp() {
        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }

        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()
        usersDao = db.usersDao()
        reposDao = db.reposDao()
        githubApi = mockk()
        internalErrorConverter = mockk()
        localDataSource = LocalDataSource(usersDao, mockk(), reposDao)
        remoteDataSource = RemoteDataSource(githubApi)
        githubRepository = GithubRepositoryImpl(
            localDataSource,
            remoteDataSource
        )
        reposBoundaryCallback = ReposBoundaryCallback(githubRepository, internalErrorConverter)
        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
    }

    @After
    fun tearDown() {
        db.close()
        RxJavaPlugins.reset()
    }

    @Test
    fun `retrieved data from online api should be properly saved offline`() {
        val newListOfRepos = mutableListOf(
            ReposResponse(
                id = 1,
                name = "first",
                description = "first description",
                stars = 10
            ),
            ReposResponse(
                id = 2,
                name = "second",
                description = "second description",
                stars = 5
            )
        )

        val newListOfReposEntity = mutableListOf(
            RepoEntity(
                repoId = 1,
                name = "first",
                description = "first description",
                stars = 10
            ),
            RepoEntity(
                repoId = 2,
                name = "second",
                description = "second description",
                stars = 5
            )
        )
        every { githubRepository.getRepositoriesOnline(any(), any()) } returns Single.just(
            newListOfRepos
        )
        reposBoundaryCallback.onZeroItemsLoaded()
        val testObserver = reposDao.getAllRepositoriesByStars().test().await()
        testObserver.assertNoErrors()
        testObserver.assertComplete()
        val values = testObserver.values()[0]
        testObserver.assertValues(newListOfReposEntity)
        Truth.assertThat(values).hasSize(2)
        Truth.assertThat(values).containsExactlyElementsIn(
            newListOfReposEntity
        )
    }
}