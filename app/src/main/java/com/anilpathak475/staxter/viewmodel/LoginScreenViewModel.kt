package com.anilpathak475.staxter.viewmodel

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.anilpathak475.staxter.R
import com.anilpathak475.staxter.data.GithubRepository
import com.anilpathak475.staxter.data.LoginRepository
import com.anilpathak475.staxter.data.model.User
import com.anilpathak475.staxter.data.model.login.LoginFormState
import com.anilpathak475.staxter.data.model.login.LoginResult
import com.anilpathak475.staxter.extension.Event
import com.anilpathak475.staxter.extension.isNotDisposed
import com.anilpathak475.staxter.store.InternalErrorConverter
import com.anilpathak475.staxter.store.security.BiometricCryptoObject
import com.anilpathak475.staxter.store.security.BiometricState
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class LoginScreenViewModel @Inject constructor(
    private val loginRepository: LoginRepository,
    private val githubRepository: GithubRepository,
    private val internalErrorConverter: InternalErrorConverter
) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    private var loginDisposable: Disposable? = null
    private var registeredUserDisposable: Disposable? = null

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    private val _biometricEvent = MutableLiveData<Event<BiometricState>>()
    val biometricEvent: LiveData<Event<BiometricState>> = _biometricEvent

    private val _isBiometricStillAvailable = MutableLiveData<Event<BiometricCryptoObject>>()
    val isBiometricStillAvailable: LiveData<Event<BiometricCryptoObject>> =
        _isBiometricStillAvailable

    fun login(username: String, password: String) {
        _loginResult.value = LoginResult.Loading
        val loginState = credentialsValidation(username, password)
        if (!loginState.isDataValid) {
            _loginResult.value = LoginResult.Error(loginState)
            return
        }

        if (loginDisposable.isNotDisposed()) {
            return
        }

        loginDisposable = loginRepository.login(username, password)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { user -> processSuccess(user) },
                { throwable -> processError(throwable) }
            )
        loginDisposable?.let { compositeDisposable.add(it) }
    }

    private fun processSuccess(user: User) {
        if (user.name == null) {
            _loginResult.value = LoginResult.Error(
                LoginFormState(
                    isDataValid = false,
                    passwordError = R.string.invalid_username_or_password,
                    emailError = R.string.invalid_username_or_password
                )
            )
        } else {
            _loginResult.value = LoginResult.Success(LoginFormState(isDataValid = true))
        }
    }

    private fun processError(message: Throwable) {
        val internalErrorType = internalErrorConverter.convertToGeneralErrorType(message)
        _loginResult.value = LoginResult.Error(LoginFormState(errorType = internalErrorType))
    }

    private fun credentialsValidation(
        username: String,
        password: String
    ): LoginFormState {
        val loginState = LoginFormState(isDataValid = true)
        with(loginState) {
            if (!isUserNameValid(username)) {
                emailError = R.string.invalid_username
                isDataValid = false
            }
            if (!isPasswordValid(password)) {
                passwordError = R.string.invalid_password
                isDataValid = false
            }
        }
        return loginState
    }

    private fun isUserNameValid(username: String): Boolean {
        return if (username.contains('@')) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            username.isNotBlank()
        }
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
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
                { processRegisteredUserForBiometricError() }
            )
        registeredUserDisposable?.let { compositeDisposable.add(it) }
    }

    private fun processRegisteredUserForBiometricError() {
        _biometricEvent.value = Event(BiometricState.USER_NOT_REGISTERED)
        _loginResult.value = LoginResult.Error(LoginFormState())
    }

    private fun processRegisteredUserForBiometricLoginSuccess(user: User) {
        if (user.name == null) {
            _biometricEvent.value = Event(BiometricState.USER_NOT_REGISTERED)
        } else {
            loginRepository.createBiometricKey()
            _biometricEvent.value = Event(BiometricState.USER_REGISTERED)
        }
    }

    fun getRegisteredUser() {

        if (registeredUserDisposable.isNotDisposed()) {
            return
        }

        registeredUserDisposable = loginRepository.getRegisteredUser()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { user -> processRegisteredUserSuccess(user) },
                { processRegisteredUserError() }
            )
        registeredUserDisposable?.let { compositeDisposable.add(it) }
    }

    private fun processRegisteredUserError() {
        _loginResult.value = LoginResult.Error(LoginFormState())
    }

    private fun processRegisteredUserSuccess(user: User) {
        if (user.name == null) {
            _loginResult.value = LoginResult.Error(LoginFormState(isDataValid = false))
        } else {
            _loginResult.value =
                LoginResult.Success(LoginFormState(isDataValid = false, userEmail = user.name))
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
    }
}