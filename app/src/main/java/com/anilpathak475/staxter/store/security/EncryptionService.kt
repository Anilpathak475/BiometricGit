package com.anilpathak475.staxter.store.security

import android.content.Context
import android.os.Build
import android.security.keystore.KeyPermanentlyInvalidatedException
import androidx.biometric.BiometricPrompt
import com.anilpathak475.staxter.store.dagger.AppScope
import javax.crypto.Cipher
import javax.inject.Inject

@AppScope
class EncryptionServices @Inject constructor(
    context: Context
) {

    companion object {
        const val DEFAULT_KEY_STORE_NAME = "default_keystore"
        const val MASTER_KEY = "MASTER_KEY"
        const val BIOMETRIC_KEY = "BIOMETRIC_KEY"
    }

    private val keyStoreWrapper = KeyStoreWrapper(context, DEFAULT_KEY_STORE_NAME)

    fun createMasterKey(password: String? = null) {
        if (isMarshmallow()) {
            createAndroidSymmetricKey()
        } else {
            createDefaultSymmetricKey(password ?: "")
        }
    }

    private fun removeMasterKey() {
        keyStoreWrapper.removeAndroidKeyStoreKey(MASTER_KEY)
    }

    private fun removeBiometricKey() {
        keyStoreWrapper.removeAndroidKeyStoreKey(BIOMETRIC_KEY)
    }

    fun encrypt(data: String, keyPassword: String? = null): String {
        return if (isMarshmallow()) {
            encryptWithAndroidSymmetricKey(data)
        } else {
            encryptWithDefaultSymmetricKey(data, keyPassword ?: "")
        }
    }

    fun decrypt(data: String, keyPassword: String? = null): String {
        return if (isMarshmallow()) {
            decryptWithAndroidSymmetricKey(data)
        } else {
            decryptWithDefaultSymmetricKey(data, keyPassword ?: "")
        }
    }

    private fun createAndroidSymmetricKey() {
        keyStoreWrapper.createAndroidKeyStoreSymmetricKey(MASTER_KEY)
    }

    private fun encryptWithAndroidSymmetricKey(data: String): String {
        val masterKey = keyStoreWrapper.getAndroidKeyStoreSymmetricKey(MASTER_KEY)
        return CipherWrapper(CipherWrapper.TRANSFORMATION_SYMMETRIC).encrypt(data, masterKey, true)
    }

    private fun decryptWithAndroidSymmetricKey(data: String): String {
        val masterKey = keyStoreWrapper.getAndroidKeyStoreSymmetricKey(MASTER_KEY)
        return CipherWrapper(CipherWrapper.TRANSFORMATION_SYMMETRIC).decrypt(data, masterKey, true)
    }

    private fun createDefaultSymmetricKey(password: String) {
        keyStoreWrapper.createDefaultKeyStoreSymmetricKey(MASTER_KEY, password)
    }

    private fun encryptWithDefaultSymmetricKey(data: String, keyPassword: String): String {
        val masterKey = keyStoreWrapper.getDefaultKeyStoreSymmetricKey(MASTER_KEY, keyPassword)
        return CipherWrapper(CipherWrapper.TRANSFORMATION_SYMMETRIC).encrypt(data, masterKey, true)
    }

    private fun decryptWithDefaultSymmetricKey(data: String, keyPassword: String): String {
        val masterKey = keyStoreWrapper.getDefaultKeyStoreSymmetricKey(MASTER_KEY, keyPassword)
        return masterKey?.let {
            CipherWrapper(CipherWrapper.TRANSFORMATION_SYMMETRIC).decrypt(
                data,
                masterKey,
                true
            )
        } ?: ""
    }

    fun prepareBiometricCryptoObject(): BiometricCryptoObject {
        return if (isMarshmallow()) {
            try {
                val symmetricKey = keyStoreWrapper.getAndroidKeyStoreSymmetricKey(BIOMETRIC_KEY)
                val cipher = CipherWrapper(CipherWrapper.TRANSFORMATION_SYMMETRIC).cipher
                cipher.init(Cipher.ENCRYPT_MODE, symmetricKey)
                BiometricCryptoObject(
                    BiometricPrompt.CryptoObject(cipher),
                    biometricErrorType = null
                )
            } catch (e: Throwable) {
                if (e is KeyPermanentlyInvalidatedException) {
                    return BiometricCryptoObject(biometricErrorType = BiometricErrorType.KEY_PERMANENTLY_INVALIDATED)
                }
                return BiometricCryptoObject(biometricErrorType = BiometricErrorType.OTHER_BIOMETRIC_ERROR)
            }
        } else BiometricCryptoObject(biometricErrorType = BiometricErrorType.OTHER_BIOMETRIC_ERROR)
    }

    fun createBiometricKey() {
        if (isMarshmallow()) {
            if (!keyStoreWrapper.isKeyExist(BIOMETRIC_KEY)) {
                keyStoreWrapper.createAndroidKeyStoreSymmetricKey(
                    BIOMETRIC_KEY,
                    userAuthenticationRequired = true,
                    invalidatedByBiometricEnrollment = true,
                    userAuthenticationValidityDurationSeconds = true
                )
            }
        }
    }

    private fun isMarshmallow() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
    fun removeAllKeys() {
        removeMasterKey()
        removeBiometricKey()
    }

}