package com.example.myapplication

import android.app.Application
import android.content.Context
import android.media.audiofx.DynamicsProcessing
import android.util.Log
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

object RetrofitObject {

    private fun getRetrofit(context: Context): Retrofit{

        val cache = Cache(File(context.cacheDir,"HTTP_Cache"),10 * 1024 * 1024L)

        val client = OkHttpClient.Builder()
            .cache(cache)
            .build()

        return Retrofit.Builder()
            .client(client)
            .baseUrl("https://acnhapi.com/v1/")
            .addConverterFactory(GsonConverterFactory.create()).build()
    }

    fun getRetrofitService(context: Context): Interface{
        Log.d("TAG", "getRetrofitService")
        return getRetrofit(context).create(Interface::class.java)
    }

}