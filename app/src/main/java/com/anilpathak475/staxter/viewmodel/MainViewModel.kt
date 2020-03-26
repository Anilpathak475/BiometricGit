package com.anilpathak475.staxter.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.anilpathak475.staxter.data.GithubRepository
import com.anilpathak475.staxter.data.LoginRepository
import com.anilpathak475.staxter.data.model.User
import com.anilpathak475.staxter.extension.Event
import com.anilpathak475.staxter.extension.isNotDisposed
import com.anilpathak475.staxter.store.security.BiometricCryptoObject
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val loginRepository: LoginRepository,
    private val githubRepository: GithubRepository
) : ViewModel() {

    private val _countdownNotification = MutableLiveData<Event<Boolean>>()
    val countdownNotification: LiveData<Event<Boolean>> = _countdownNotification

    private val compositeDisposable = CompositeDisposable()
    private var timerDisposable: Disposable? = null
    private var registeredUserDisposable: Disposable? = null
    private var connectivityDisposable: Disposable? = null

    private val _internetState = MutableLiveData<Boolean>()
    val internetState: MutableLiveData<Boolean> = _internetState

    private val _biometricEvent = MutableLiveData<Event<Boolean>>()
    val biometricEvent: LiveData<Event<Boolean>> = _biometricEvent

    private val _isBiometricStillAvailable = MutableLiveData<Event<BiometricCryptoObject>>()
    val isBiometricStillAvailable: LiveData<Event<BiometricCryptoObject>> =
        _isBiometricStillAvailable

    private val _logoutEvent = MutableLiveData<Event<Boolean>>()
    val logoutEvent: LiveData<Event<Boolean>> = _logoutEvent

    private fun changeInternetState(internetState: Boolean) {
        _internetState.value = internetState
    }

    fun startNotificationTimer() {
        if (timerDisposable.isNotDisposed()) {
            return
        }
        timerDisposable = Observable.interval(TIME_TO_TICK, TimeUnit.MINUTES)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    if (isUserLogIn()) {
                        _countdownNotification.value = Event(true)
                    }
                },
                { throwable -> Timber.d("Timer Error $throwable") }
            )
        timerDisposable?.let { compositeDisposable.add(it) }

    }

    private fun isUserLogIn(): Boolean {
        return loginRepository.isUserLogIn()
    }

    override fun onCleared() {
        super.onCleared()
        stopNotificationTimer()
        compositeDisposable.clear()
    }

    fun stopNotificationTimer() {
        compositeDisposable.clear()
    }

    fun tryToUseBiometric() {
        if (registeredUserDisposable.isNotDisposed()) {
            return
        }
        registeredUserDisposable = loginRepository.getRegisteredUser()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { user -> processRegisteredUserForBiometricLoginSuccess(user) },
                { Timber.d("Error while retrieving users") }
            )
        registeredUserDisposable?.let {
            compositeDisposable.add(it)
        }
    }

    private fun processRegisteredUserForBiometricLoginSuccess(user: User) {
        if (user.name != null) {
            createBiometricKey()
            _biometricEvent.value = Event(true)
        }
    }

    fun isNewBiometric() {
        val biometricCryptoObject = loginRepository.getBiometricPrompt()
        _isBiometricStillAvailable.value = Event(biometricCryptoObject)
    }

    fun clearAllData() {
        val disposable = Schedulers.single().scheduleDirect {
            loginRepository.clearAllData()
            githubRepository.clearAllData()
        }
        compositeDisposable.add(disposable)
        _logoutEvent.value = Event(true)
    }

    private fun createBiometricKey() {
        loginRepository.createBiometricKey()
    }

    fun logoutUser() {
        loginRepository.loginUserState(false)
    }

    fun logInUser() {
        loginRepository.loginUserState(true)
    }

    fun observeInternetState() {
        connectivityDisposable = ReactiveNetwork
            .observeInternetConnectivity()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ networkAvailability ->
                if (networkAvailability) {
                    changeInternetState(true)
                    Timber.d("Connectivity state: ON")
                } else {
                    changeInternetState(false)
                    Timber.d("Connectivity state: OFF")
                }
            },
                {
                    changeInternetState(false)
                    Timber.d("Connectivity state: ERROR")
                })

        compositeDisposable.let {
            compositeDisposable.add(it)
        }
    }

    companion object {
        const val TIME_TO_TICK: Long = 10
    }
}