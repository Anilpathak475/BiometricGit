package com.anilpathak475.staxter.data.model.login

sealed class LoginResult {
    object Loading : LoginResult()
    data class Error(val loginFormState: LoginFormState?) : LoginResult()
    data class Success(val loginFormState: LoginFormState?) : LoginResult()
}