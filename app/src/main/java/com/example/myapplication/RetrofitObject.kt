package com.example.myapplication

import android.app.Application
import android.content.Context
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

object RetrofitObject {

    private fun getRetrofit(): Retrofit{

        return Retrofit.Builder()
            .baseUrl("https://acnhapi.com/v1/")
            .addConverterFactory(GsonConverterFactory.create()).build()
    }

    fun getRetrofitService(): Interface{
        return getRetrofit().create(Interface::class.java)
    }

}