package com.anilpathak475.staxter.data.model.login

data class LoginFormState(
    var emailError: Int? = null,
    var userEmail: String? = null,
    var passwordError: Int? = null,
    var isDataValid: Boolean = false,
    var errorType: ErrorType? = null,
    var newBiometric: Boolean? = null
)