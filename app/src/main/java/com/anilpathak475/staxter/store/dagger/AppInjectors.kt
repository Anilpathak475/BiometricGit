package com.anilpathak475.staxter.store.dagger


import com.anilpathak475.staxter.ui.MainActivity
import com.anilpathak475.staxter.ui.fragments.GithubRepoFragment
import com.anilpathak475.staxter.ui.fragments.GithubReposFragment
import com.anilpathak475.staxter.ui.fragments.LoginScreenFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class AppInjectors {

    @ContributesAndroidInjector
    abstract fun mainActivity(): MainActivity

    @ContributesAndroidInjector
    abstract fun loginScreenFragment(): LoginScreenFragment

    @ContributesAndroidInjector
    abstract fun githubReposFragment(): GithubReposFragment

    @ContributesAndroidInjector
    abstract fun githubRepoFragment(): GithubRepoFragment

}