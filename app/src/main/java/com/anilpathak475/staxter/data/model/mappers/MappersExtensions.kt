package com.anilpathak475.staxter.data.model.mappers

import com.anilpathak475.staxter.data.model.User
import com.anilpathak475.staxter.store.db.UserEntity

fun UserEntity.mapToUser(): User {
    return User(name = this.email)
}