package com.example.myapplication

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Fish::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

        abstract fun getFishDao(): Dao

        companion object {
                private const val DB_NAME = "fish"
                private var instance: AppDatabase? = null

                fun getInstance(application : Application): AppDatabase? { // singleton pattern
                        if (instance == null) {
                                synchronized(this){
                                        instance = Room.databaseBuilder(application, AppDatabase::class.java, DB_NAME).build()
                                }
                        }
                        return instance
                }
        }
}