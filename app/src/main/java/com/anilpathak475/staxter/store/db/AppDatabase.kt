package com.anilpathak475.staxter.store.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.anilpathak475.staxter.base.SingletonHolder
import com.anilpathak475.staxter.store.db.converter.UuidConverter

@Database(
    entities = [
        UserEntity::class,
        RepoEntity::class
    ],
    version = 1
)
@TypeConverters(
    value = [
        UuidConverter::class
    ]
)
internal abstract class AppDatabase : RoomDatabase() {

    abstract fun usersDao(): UsersDao
    abstract fun reposDao(): ReposDao

    companion object : SingletonHolder<AppDatabase, Context>({
        Room.databaseBuilder(it.applicationContext, AppDatabase::class.java, "staxter.db")
            .build()
    })
}


