package com.anilpathak475.staxter

import com.anilpathak475.staxter.data.model.User
import com.anilpathak475.staxter.store.db.UserEntity
import java.util.UUID

const val ANY_ERROR = "Any error !"

val NEW_VALID_USER = User(
    name = "testName"
)

val NEW_EMPTY_USER = User(
    name = null
)

val NEW_EMPTY_USER_ENTITY = UserEntity(
    userId = UUID.randomUUID(),
    email = "testName",
    password = "encryptedPassword"
)