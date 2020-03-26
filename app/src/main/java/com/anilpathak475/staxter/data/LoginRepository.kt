package com.anilpathak475.staxter.data

import com.anilpathak475.staxter.data.model.User
import com.anilpathak475.staxter.store.security.BiometricCryptoObject
import io.reactivex.Single

interface LoginRepository {
    fun login(email: String, password: String): Single<User>
    fun getRegisteredUser(): Single<User>
    fun getBiometricPrompt(): BiometricCryptoObject
    fun clearAllData()
    fun createBiometricKey()
    fun loginUserState(isLogIn: Boolean)
    fun isUserLogIn(): Boolean
}
