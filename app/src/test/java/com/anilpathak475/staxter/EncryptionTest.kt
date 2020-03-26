package com.anilpathak475.staxter

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.anilpathak475.staxter.store.security.CipherWrapper
import com.anilpathak475.staxter.store.security.KeyStoreWrapper
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EncryptionTest {

    companion object {
        const val TEST_PASSWORD = "TEST_PASSWORD"
    }

    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
    }

    @Test
    @Ignore
    fun testEncryption() {
        val keyStore = KeyStoreWrapper(context, "default_keystore")
        val symmetricKey = keyStore.createAndroidKeyStoreSymmetricKey(TEST_PASSWORD)

        val cipher = CipherWrapper(CipherWrapper.TRANSFORMATION_SYMMETRIC)

        val encryptedData = cipher.encrypt(TEST_PASSWORD, symmetricKey)
        val decryptedData = cipher.decrypt(encryptedData, symmetricKey)

        assertThat(encryptedData).isEqualTo(decryptedData)
    }
}