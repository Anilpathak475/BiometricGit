package com.anilpathak475.staxter.store.dagger

import android.content.Context
import com.anilpathak475.staxter.data.GithubRepository
import com.anilpathak475.staxter.data.GithubRepositoryImpl
import com.anilpathak475.staxter.data.LoginRepository
import com.anilpathak475.staxter.data.LoginRepositoryImpl
import com.anilpathak475.staxter.data.api.GithubApi
import com.anilpathak475.staxter.notification.SimpleNotificationManager
import com.anilpathak475.staxter.notification.SimpleNotificationManagerImpl
import com.anilpathak475.staxter.store.InternalErrorConverter
import com.anilpathak475.staxter.store.InternalErrorConverterImpl
import com.anilpathak475.staxter.store.db.LocalDataSource
import com.anilpathak475.staxter.store.db.RemoteDataSource
import com.anilpathak475.staxter.store.security.EncryptionServices
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module
class AppModule {

    @Provides
    fun provideRepository(
        localDataSource: LocalDataSource,
        encryptionServices: EncryptionServices
    ): LoginRepository {
        return LoginRepositoryImpl(
            localDataSource,
            encryptionServices
        )
    }

    @Provides
    fun provideGithubRepository(
        localDataSource: LocalDataSource,
        remoteDataSource: RemoteDataSource
    ): GithubRepository {
        return GithubRepositoryImpl(
            localDataSource,
            remoteDataSource
        )
    }

    @Provides
    fun providesServerErrorConverter(): InternalErrorConverter {
        return InternalErrorConverterImpl()
    }

    @Provides
    fun providesEncryptionServices(context: Context): EncryptionServices {
        return EncryptionServices(context)
    }

    @Provides
    fun providesSimpleNotificationManager(): SimpleNotificationManager {
        return SimpleNotificationManagerImpl()
    }

    @AppScope
    @Provides
    fun provideGitHubApi(
        retrofit: Retrofit
    ): GithubApi {
        return retrofit.create(GithubApi::class.java)
    }

}