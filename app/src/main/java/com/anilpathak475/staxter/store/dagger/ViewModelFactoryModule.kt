package com.anilpathak475.staxter.store.dagger

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.anilpathak475.staxter.viewmodel.GithubRepoViewModel
import com.anilpathak475.staxter.viewmodel.GithubReposViewModel
import com.anilpathak475.staxter.viewmodel.LoginScreenViewModel
import com.anilpathak475.staxter.viewmodel.MainViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelFactoryModule {

    @Binds
    abstract fun bindViewModelFactory(viewModelFactory: DaggerViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(LoginScreenViewModel::class)
    abstract fun bindLoginScreenViewModel(loginScreenViewModel: LoginScreenViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(GithubReposViewModel::class)
    abstract fun bindGithubReposViewModel(githubReposViewModel: GithubReposViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(GithubRepoViewModel::class)
    abstract fun bindGithubRepoViewModel(githubRepoViewModel: GithubRepoViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    abstract fun bindMainViewModel(mainViewModel: MainViewModel): ViewModel

}