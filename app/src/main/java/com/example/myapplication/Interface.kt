package com.example.myapplication

import retrofit2.http.GET
import retrofit2.http.Path

interface Interface {
    @GET("fish/{fishID}")
    suspend fun getName(
        @Path("fishID") fishID: String
    ): FishName

}