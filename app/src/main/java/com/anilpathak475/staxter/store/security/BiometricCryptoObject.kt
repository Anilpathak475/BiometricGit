package com.anilpathak475.staxter.store.security

import androidx.biometric.BiometricPrompt

data class BiometricCryptoObject(
    var biometricCryptoObject: BiometricPrompt.CryptoObject? = null,
    var biometricErrorType: BiometricErrorType? = null
)