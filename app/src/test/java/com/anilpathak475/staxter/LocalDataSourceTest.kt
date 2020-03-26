package com.anilpathak475.staxter

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.anilpathak475.staxter.store.db.AppDatabase
import com.anilpathak475.staxter.store.db.UserEntity
import com.anilpathak475.staxter.store.db.githubrepo.ReposDao
import com.anilpathak475.staxter.store.db.user.UsersDao
import io.mockk.MockKAnnotations
import io.mockk.clearAllMocks
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID


@RunWith(AndroidJUnit4::class)
class LocalDataSourceTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var usersDao: UsersDao
    private lateinit var reposDao: ReposDao
    private lateinit var db: AppDatabase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        clearAllMocks()
        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }

        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()
        usersDao = db.usersDao()
        reposDao = db.reposDao()
        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
    }

    @After
    fun tearDown() {
        db.close()
        RxJavaPlugins.reset()
    }

    @Test
    fun `registered user should be retrieved properly`() {
        val id = UUID.randomUUID()
        val email = "ij@wp.pl"
        val password = "12345678"
        usersDao.insert(
            UserEntity(
                userId = id,
                email = email,
                password = password
            )
        )
        val testObserver = usersDao.getRegisteredUser().test().await()
        testObserver.assertNoErrors()
        testObserver.assertComplete()
        testObserver.assertValue {
            it.email == email
        }
        testObserver.assertValue {
            it.password == password
        }
        testObserver.assertValue {
            it.userId == id
        }
    }
}