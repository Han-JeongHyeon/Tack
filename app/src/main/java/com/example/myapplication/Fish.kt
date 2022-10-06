package com.example.myapplication

import com.google.gson.annotations.SerializedName
import retrofit2.http.Url

data class FishName(
    //"이름":{"변수 이름1":"값1","변수 이름2":"값2"}일 때 이런식으로 표현한다
    val name: Name,
    @SerializedName("icon_uri") val image : String,
    @SerializedName("price") val price : String
)

data class Name(
    @SerializedName("name-KRko") val KRko : String
)
