package com.anilpathak475.staxter.store.dagger.core

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import dagger.Module
import dagger.Provides

@Module
class MainAppModule {

    @Provides
    internal fun provideSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences("STAXTER_PREF", Context.MODE_PRIVATE)
    }

    @Provides
    fun providesResources(context: Context): Resources {
        return context.resources
    }
}