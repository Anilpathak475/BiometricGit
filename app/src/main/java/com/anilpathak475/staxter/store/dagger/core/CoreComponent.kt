package com.anilpathak475.staxter.store.dagger.core

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import com.anilpathak475.staxter.store.db.ReposDao
import com.anilpathak475.staxter.store.db.UsersDao
import dagger.BindsInstance
import dagger.Component
import retrofit2.Retrofit
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        MainAppModule::class,
        NetworkModule::class,
        AppDatabaseModule::class
    ]
)
interface CoreComponent {

    fun sharedPref(): SharedPreferences
    fun resources(): Resources
    fun retrofit(): Retrofit
    fun userDao(): UsersDao
    fun reposDao(): ReposDao

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun context(context: Context): Builder

        fun build(): CoreComponent
    }

}