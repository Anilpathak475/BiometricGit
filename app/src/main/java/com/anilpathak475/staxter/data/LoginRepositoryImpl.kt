package com.anilpathak475.staxter.data

import com.anilpathak475.staxter.data.model.User
import com.anilpathak475.staxter.data.model.mappers.mapToUser
import com.anilpathak475.staxter.store.db.LocalDataSource
import com.anilpathak475.staxter.store.db.UserEntity
import com.anilpathak475.staxter.store.security.BiometricCryptoObject
import com.anilpathak475.staxter.store.security.EncryptionServices
import io.reactivex.Single
import java.util.UUID
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(
    private val localDataSource: LocalDataSource,
    private val encryptionService: EncryptionServices
) : LoginRepository {

    override fun clearAllData() {
        localDataSource.deleteAllUsers()
        encryptionService.removeAllKeys()
    }

    override fun getRegisteredUser(): Single<User> {
        return localDataSource.getRegisteredUser()
            .map { it.mapToUser() }
    }

    override fun login(email: String, password: String): Single<User> {
        return localDataSource.getUserByEmail(email)
            .flatMap { userList ->
                val user: UserEntity
                user = if (userList.isEmpty()) {
                    createKeys(password)
                    val encryptedPassword = encryptionService.encrypt(password, password)
                    val newUserEntity =
                        UserEntity(
                            UUID.randomUUID(),
                            email = email,
                            password = encryptedPassword
                        )
                    localDataSource.insertUser(newUserEntity)
                    newUserEntity
                } else {
                    userList.first()
                }
                return@flatMap if (encryptionService.decrypt(user.password, password) == password) {
                    val newUser = user.mapToUser()
                    Single.just(newUser)
                } else {
                    Single.just(User())
                }
            }
    }

    private fun createKeys(password: String) {
        encryptionService.createMasterKey(password)
    }

    override fun createBiometricKey() {
        encryptionService.createBiometricKey()
    }

    override fun loginUserState(isLogIn: Boolean) {
        localDataSource.loginUserState(isLogIn)
    }

    override fun isUserLogIn(): Boolean {
        return localDataSource.isUserLogIn()
    }

    override fun getBiometricPrompt(): BiometricCryptoObject {
        return encryptionService.prepareBiometricCryptoObject()
    }
}
