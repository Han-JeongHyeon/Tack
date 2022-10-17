package com.example.myapplication

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface Interface {
    @GET("fish/{fishID}")
    fun getName(
        @Path("fishID") fishID: String
    ): Call<FishName>

}