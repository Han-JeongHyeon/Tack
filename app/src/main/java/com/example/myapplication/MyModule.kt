package com.example.myapplication

import android.app.Application
import android.content.Context
import androidx.room.Room
import okhttp3.Cache
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

var appModule = module {

    fun fishListDatabase(application: Application) : AppDatabase {
        return Room.databaseBuilder(application, AppDatabase::class.java, "fish")
            .fallbackToDestructiveMigration()
            .build()
    }

    fun fishListDao(database: AppDatabase) : Dao {
        return database.getFishDao()
    }

    single { fishListDatabase( androidApplication() ) }
    single { fishListDao( get() ) }

    single { RetrofitObject( get() ) }
    single { Repository( get() ) }
}

var viewModule = module{
    viewModel {
        MainViewModel( get(), get(), get() )
    }
}