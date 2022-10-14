package com.example.myapplication

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Fishs::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
        abstract fun userDao(): Dao
}