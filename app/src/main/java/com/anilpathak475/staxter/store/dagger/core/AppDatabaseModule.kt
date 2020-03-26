package com.anilpathak475.staxter.store.dagger.core

import android.content.Context
import com.anilpathak475.staxter.store.db.AppDatabase
import com.anilpathak475.staxter.store.db.ReposDao
import com.anilpathak475.staxter.store.db.UsersDao
import dagger.Module
import dagger.Provides

@Module
class AppDatabaseModule {

    @Provides
    internal fun appDatabase(context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }

    @Provides
    internal fun userDao(appDatabase: AppDatabase): UsersDao {
        return appDatabase.usersDao()
    }

    @Provides
    internal fun reposDao(appDatabase: AppDatabase): ReposDao {
        return appDatabase.reposDao()
    }

}