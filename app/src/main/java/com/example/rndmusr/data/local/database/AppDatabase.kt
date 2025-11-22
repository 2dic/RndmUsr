package com.example.rndmusr.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.rndmusr.data.local.dao.UserDao
import com.example.rndmusr.data.local.entity.UserEntity

@Database(
    entities = [UserEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}