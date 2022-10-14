package com.example.myapplication

import android.util.Log
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface Interface {
    @GET("fish/{fishID}")
    fun getName(
        @Path("fishID") fishID: String
    ): Call<FishName>

}